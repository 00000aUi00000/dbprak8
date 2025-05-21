package com.backend.service;

import java.util.HashMap;
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
import com.backend.service.util.Result;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Slf4j
@Service
public class ShopDatabaseParser {

    private final FilialeRepository filialeRepository;
    private final MusikCDImportParser musikCDImportParser;
    private final DVDImportParser dvdImportParser;
    private final BookImportParser bookImportParser;

    private final Map<String, ProduktImportParser> produktParser = new HashMap<>();


    @PostConstruct
    private void initParsers() {
        produktParser.put("Music", musikCDImportParser);
        produktParser.put("DVD", dvdImportParser);
        produktParser.put("Book", bookImportParser);
    }

    @Transactional
    public void parseData(final ShopData shopData) {
        final Result<Filiale> filiale = parseFiliale(shopData);

        if (filiale.isError()) {
            String msg = "Could not parse filiale: " + filiale.getErrorMessage();
            ImportLogger.logError(msg, null, null);
            log.error(msg);
            return;
        }

        for (final ItemData itemData : shopData.getItems()) {
            final Result<Void> result = parseItemData(filiale.getValue(), itemData);

            if (result.isError()) {
                String msg = ("Could not parse item: " + result.getErrorMessage());
                log.error(msg);
                //ImportLogger.logError("ParseData", itemData, msg);
            }

        }
    }

    private Result<Filiale> parseFiliale(ShopData shopData) {
        final String name = shopData.getName();
        final String street = shopData.getStreet();

        if (name == null) {
            return Result.error("The name of the given shop is null.");
        }

        if (street == null) {
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

    private Result<Void> parseItemData(Filiale filiale, ItemData itemData) {

        if (itemData == null) {
            String msg = "The provided item data is null.";
            ImportLogger.logError("parseItemData", itemData, msg);
            return Result.error(msg);
        }

        if (itemData.getPgroup() == null) {
            String msg = "The group of the provided item is null.";
            ImportLogger.logError("parseItemData", itemData, msg);
            return Result.error(msg);
        }

        final ProduktImportParser produktImportParser = this.produktParser.get(itemData.getPgroup().trim());


        if (produktImportParser == null) {
            String msg = "The product type " + itemData.getPgroup() + " is not registered. [Ignored]";
            ImportLogger.logError("produktImportParser", itemData, msg);
            return Result.error(msg);
        }

        final Result<? extends Produkt> produkt = produktImportParser.parseProdukt(itemData);

        // wenn es einen Fehler bei der Produkterstellung gab, breche ab und leite diesen weiter
        if (produkt.isError()) {
            //ImportLogger.logError("produktError", itemData, produkt.getErrorMessage());
            return Result.error(produkt.getErrorMessage());
        }

        // TBD prüfen ob Angebot vor Angebotdetails eingepflegt wird
        final Produkt produktValue = produkt.getValue();
        final Result<Angebot> angebot = produktImportParser.parseAngebot(filiale, produktValue);
        final Result<Angebotsdetails> angebotdetails = produktImportParser.parseAngebotdetails(angebot.getValue(),
                itemData);

        produktValue.addAngebot(angebot.getValue());
        filiale.addAngebot(angebot.getValue());

        if (angebotdetails.isError()) {
            String msg = "Could not parse angebot details: " + angebotdetails.getErrorMessage();
            ImportLogger.logError("angebotDetailsError", itemData, msg);
            return Result.error(msg);
        }

        return Result.empty();
    }

}