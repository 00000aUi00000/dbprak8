package com.backend.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.backend.entity.Angebot;
import com.backend.entity.Angebotsdetails;
import com.backend.entity.Filiale;
import com.backend.entity.Produkt;
import com.backend.repository.FilialeRepository;
import com.backend.service.dto.ItemData;
import com.backend.service.dto.ShopData;
import com.backend.service.parser.BookImportParser;
import com.backend.service.parser.DVDImportParser;
import com.backend.service.parser.MusikCDImportParser;
import com.backend.service.parser.ProduktImportParser;
import com.backend.service.util.ImportLogger;
import com.backend.service.util.ImportStatistik;
import com.backend.service.util.Result;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

// Klasse zum Laden Shop-bezogener Daten
@RequiredArgsConstructor
@Slf4j
@Service
public class ShopDatabaseParser {

    private final FilialeRepository filialeRepository;
    private final MusikCDImportParser musikCDImportParser;
    private final DVDImportParser dvdImportParser;
    private final BookImportParser bookImportParser;

    private final Map<String, ProduktImportParser> produktParser = new HashMap<>();

    // Init Map nach vollständiger Objekt-Initialisierung
    @PostConstruct
    private void initParsers() {
        // Parser für 3 vorkonfigurierte Produkttypen
        produktParser.put("Music", musikCDImportParser);
        produktParser.put("DVD", dvdImportParser);
        produktParser.put("Book", bookImportParser);
    }

    /**
     * Parst die Daten in der gegebenen ShopData (Filiale, Produkte) und
     * loggt eventuelle Fehler in der Konsole und einer Datei.
     * 
     * @param shopData die Shop Daten zum Parsen
     */
    @Transactional // Hibernate-Annotation, Ausführen aller DB-Operationen innerhalb der Methode in einer Transaktion
    public void parseData(final ShopData shopData) {
        final Result<Filiale> filiale = parseFiliale(shopData);

        // wenn Filiale nicht geparst werden konnte: Logging des Fehlers & Abbruch
        if (filiale.isError()) {
            String msg = "Could not parse filiale: " + filiale.getErrorMessage();
            ImportLogger.logError(msg, null, null);
            log.error(msg);
            return;
        }

        for (final ItemData itemData : shopData.getItems()) {
            final Result<Void> result = parseItemData(filiale.getValue(), itemData);

            // wenn Produkt nicht geparst werden konnte: Logging des Fehlers
            if (result.isError()) {
                String msg = ("Could not parse item: " + result.getErrorMessage());
                log.error(msg);
                // Duplikat: ImportLogger.logError("ParseData", itemData, msg);
            }

        }
    }

    /**
     * Parst die Filiale in der gegebenen ShopData.
     * Gibt bereits vorhandene Filiale zurück, falls bereits eine gespeichert ist.
     * 
     * @param shopData die Shop Daten zum Parsen
     * @return Result mit der Filiale oder evtl. ein fehlerhaftes Result
     */
    private Result<Filiale> parseFiliale(ShopData shopData) {
        final String name = shopData.getName();
        final String street = shopData.getStreet();

        // Fehler wenn Name nicht vorhanden
        if (name == null) {
            ImportStatistik.increment("[Shop] name of the given shop is null");
            return Result.error("The name of the given shop is null.");
        }

        // Fehler wenn Straße nicht vorhanden
        if (street == null) {
            ImportStatistik.increment("[Shop] street of the given shop is null");
            return Result.error("The street of the given shop is null (" + name + ").");
        }

        // Rückgabe mit prüfung ob Shop schon vorhanden ist
        return filialeRepository
                .findByNameAndAnschrift(name, street)
                .map(Result::of)
                .orElseGet(() -> {
                    Filiale filiale = new Filiale();
                    filiale.setName(name);
                    filiale.setAnschrift(street);
                    filialeRepository.save(filiale);
                    return Result.of(filiale);
                });
    }

    /**
     * Parst ein Produkt mit der gegebenenden Filiale und ItemData.
     * Gibt ein leeres Result bei Erfolg und ein Fehlerhaftes bei einem Fehler zurück.
     * Der Fehler wird in einer Datei geloggt.
     * 
     * @param filiale die Filiale zur Zuordnung des Produkts
     * @param itemData die zugrundeliegenden Produktdaten
     * @return leeres Result oder evtl. ein fehlerhaftes Result
     */
    private Result<Void> parseItemData(Filiale filiale, ItemData itemData) {

        // Fehler wenn Produktdaten nicht vorhanden
        if (itemData == null) {
            ImportStatistik.increment("[Product] provided item data is null");
            String msg = "The provided item data is null.";
            ImportLogger.logError("parseItemData", itemData, msg);
            return Result.error(msg);
        }

        // Fehler wenn Typ nicht vorhanden
        if (itemData.getPgroup() == null) {
            ImportStatistik.increment("[Product] group of the provided item is null");
            String msg = "The group of the provided item is null.";
            ImportLogger.logError("parseItemData", itemData, msg);
            return Result.error(msg);
        }

        // Holen des Parsers für gegebenden Produkttyp
        final ProduktImportParser produktImportParser = this.produktParser.get(itemData.getPgroup().trim());

        // Fehler bei nicht registriertem Produkttyp
        if (produktImportParser == null) {
            ImportStatistik.increment("[Product] product type not registered");
            String msg = "The product type " + itemData.getPgroup() + " is not registered. [Ignored]";
            ImportLogger.logError("produktImportParser", itemData, msg);
            return Result.error(msg);
        }

        // Parsen der Produktdaten
        final Result<? extends Produkt> produkt = produktImportParser.parseProdukt(itemData);

        // wenn es einen Fehler bei der Produkterstellung gab, breche ab und leite diesen weiter
        if (produkt.isError()) {
            // Duplikat: ImportLogger.logError("produktError", itemData, produkt.getErrorMessage());
            return Result.error(produkt.getErrorMessage());
        }

        // Parsen von Angebot und Angebotsdetails, Setzen von Rückbeziehungen
        final Produkt produktValue = produkt.getValue();
        final Result<Angebot> angebot = produktImportParser.parseAngebot(filiale, produktValue);
        final Result<List<Angebotsdetails>> angebotdetails = produktImportParser.parseAngebotdetails(angebot.getValue(),
                itemData);

        produktValue.addAngebot(angebot.getValue());
        filiale.addAngebot(angebot.getValue());

        if (angebotdetails.isError()) {
            String msg = "Could not parse angebot details: " + angebotdetails.getErrorMessage();
            ImportLogger.logError("angebotDetailsError", itemData, msg);
            return Result.error(msg);
        }

        return Result.empty(); // leeres Result bei Erfolg
    }

}