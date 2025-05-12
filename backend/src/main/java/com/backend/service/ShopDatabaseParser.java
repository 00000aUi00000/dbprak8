package com.backend.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.backend.service.util.Result;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ShopDatabaseParser {

    @Autowired
    private FilialeRepository filialeRepository;
    @Autowired
    private MusikCDImportParser musikCDImportParser;
    @Autowired
    private DVDImportParser dvdImportParser;
    @Autowired
    private BookImportParser bookImportParser;

    private final Map<String, ProduktImportParser> produktParser;

    public ShopDatabaseParser() {
        this.produktParser = new HashMap<>();
    }

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
            log.error("Could not parse filiale: " + filiale.getErrorMessage());
            return;
        }

        for (final ItemData itemData : shopData.getItems()) {
            final Result<Void> result = parseItemData(filiale.getValue(), itemData);

            if (result.isError()) {
                // anstatt zu loggen, könnte man ie Fehler an eine extra Klasse (z.B.
                // ErrorReport) hinzufügen
                // und dort z.B. in einer Datei speichern
                log.error("Could not parse item: " + result.getErrorMessage());
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

    // @Transactional - verschoben zu parseData aufgrund der aktivhaltung der
    // Hibernate session
    private Result<Void> parseItemData(Filiale filiale, ItemData itemData) {

        if (itemData == null) {
            return Result.error("The provided item data is null.");
        }

        if (itemData.getPgroup() == null) {
            return Result.error("The group of the provided item is null.");
        }

        final ProduktImportParser produktImportParser = this.produktParser.get(itemData.getPgroup().trim());

        if (produktImportParser == null) {
            return Result.error("The product type " + itemData.getPgroup() + " is not registered.");
        }

        final Result<? extends Produkt> produkt = produktImportParser.parseProdukt(itemData);

        // wenn es einen Fehler bei der Produkterstellung gab, breche ab und leite
        // diesen weiter
        if (produkt.isError()) {
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
            return Result.error("Could not parse angebot details: " + angebotdetails.getErrorMessage());
        }

        return Result.empty();
    }

}