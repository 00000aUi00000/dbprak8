package com.backend.service.parser;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.backend.entity.Autoren;
import com.backend.entity.Buch;
import com.backend.entity.Person;
import com.backend.entity.Produkt;
import com.backend.repository.AutorenRepository;
import com.backend.repository.BuchRepository;
import com.backend.service.dto.AuthorData;
import com.backend.service.dto.BookSpecData;
import com.backend.service.dto.ISBNData;
import com.backend.service.dto.ItemData;
import com.backend.service.dto.PublisherData;
import com.backend.service.util.ImportLogger;
import com.backend.service.util.ImportStatistik;
import com.backend.service.util.ParseUtil;
import com.backend.service.util.Result;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Klasse zum Importieren von Produkten des Typs "Book"
@Slf4j
@RequiredArgsConstructor
@Service
public class BookImportParser extends ProduktImportParser {

    private final BuchRepository buchRepository;
    private final AutorenRepository autorenRepository;

    /**
     * Überschriebende Methode zum Laden eines Produkts vom Typ "Book".
     * 
     * Gibt ein fehlerhaftes Result zurück, wenn das Produkt oder Buch-bezogende
     * Daten nicht geparst werden konnten, und loggt den Fehler in Konsole und
     * Datei.
     */
    @Override
    public Result<? extends Produkt> parseProdukt(ItemData itemData) {
        // Parsen des grundlegenden Buch-Produkts
        final Result<Buch> buch = parseBuch(itemData);

        if (buch.isError()) {
            String msg = "Buch: " + buch.getErrorMessage() + " [Ignored]";
            ImportLogger.logError("BookImport", itemData, msg);
            return Result.error(msg);
        }

        // Parsen von Buch-bezogenden Daten basierend auf geparstem Buch
        final Result<Void> result = parseBuchData(buch.getValue(), itemData);

        if (result.isError()) {
            String msg = "Buch-Data: " + result.getErrorMessage() + " [Ignored]";
            ImportLogger.logError("BookDataImport", itemData, msg);
            return Result.error(msg);
        }

        return buch;
    }

    /**
     * Parst das Produkt als Buch und überprüft syntaktische sowie semantische
     * Integritäten.
     * 
     * @param itemData die zugrundeliegenden Produktdaten
     * @return Result mit erfolgreich geparstem Buch, sonst fehlerhaftes Result
     */
    private Result<Buch> parseBuch(ItemData itemData) {
        final BookSpecData bookSpecData = itemData.getBookspec();

        // Buch hat keine Buchdaten
        if (bookSpecData == null) {
            return Result.error("book spec data are empty: " + itemData.getAsin());
        }

        final String asin = itemData.getAsin();
        final String title = itemData.getTitle();
        final String picture = itemData.getPicture();
        final String salesRank = itemData.getSalesrank();
        final String publication = bookSpecData.getPublication() != null ? bookSpecData.getPublication().getValue()
                : null;
        final ISBNData isbn = bookSpecData.getIsbnData();
        final List<PublisherData> publisher = itemData.getPublisher();

        Integer seitenZahl = bookSpecData.getPages();
        Integer parsedSalesRank = ParseUtil.parseInteger(salesRank);
        LocalDate parsedPublication = ParseUtil.parseDate(publication);

        if (asin == null || asin.isBlank()) {
            ImportStatistik.increment("[Book] asin is null");
            return Result.error("asin is null.");
        }

        if (title == null || title.isBlank()) {
            ImportStatistik.increment("[Book] title is null");
            return Result.error("title is null (" + itemData.getAsin() + ").");
        }

        // Seitenzahl ist negativ: Warning in Konsole und Datei und Rückfall auf null-Wert
        if (seitenZahl != null && seitenZahl < 0) {
            ImportStatistik.increment("[Book] seitenzahl is negative");
            String msg = "seitenzahl is negative: (" + itemData.getAsin() + "). [Removed]";
            ImportLogger.logWarning("BookImport", itemData, msg);
            log.warn(msg);
            seitenZahl = null;
        }

        // Salesrank konnte nicht zu Integer umgewandelt werden oder ist negativ
        if (salesRank != null && !salesRank.isBlank() && (parsedSalesRank == null || parsedSalesRank < 0)) {
            ImportStatistik.increment("[Book] sales rank isnt integer or negative");
            String msg = "sales rank isnt integer or negative: " + salesRank + ". (" + itemData.getAsin() + "). [Removed]";
            ImportLogger.logWarning("BookImport", itemData, msg);
            log.warn(msg);
            parsedSalesRank = null;
        }

        // Publication konnte nicht zu LocalDate umgewandelt werden
        if (publication != null && !publication.isBlank() && parsedPublication == null) {
            ImportStatistik.increment("[Book] release date isnt date");
            String msg = "release date isnt date: " + publication + ". (" + itemData.getAsin() + "). [Removed]";
            ImportLogger.logWarning("BookImport", itemData, msg);
            log.warn(msg);
            parsedPublication = null;
        }

        // Verlag(e) verarbeiten
        String verlagKombiniert = null;
        if (publisher != null && !publisher.isEmpty()) {
            verlagKombiniert = publisher.stream()
                    .map(PublisherData::getName)
                    .filter(Objects::nonNull)
                    .distinct()
                    .reduce((a, b) -> a + " / " + b)
                    .orElse(null);
        }

        final Buch buch = new Buch();

        buch.setProduktId(asin);

        buch.setTitel(title);
        buch.setBild(picture);
        buch.setVerkaufsrang(salesRank == null || salesRank.isBlank() ? null : parsedSalesRank); // wenn Salesrank vorhanden, nutze geparsten Salesrank
        buch.setErscheinungsdatum(publication == null || publication.isBlank() ? null : parsedPublication); // wenn Publication vorhanden, nutze geparste Publication
        buch.setIsbn(isbn != null ? isbn.getValue() : null); // Einträge ohne ISBN sollen möglich sein
        buch.setSeitenanzahl(seitenZahl);
        buch.setVerlag(verlagKombiniert);

        buchRepository.save(buch);

        return Result.of(buch);
    }

    /**
     * Parst Buch-bezogene Daten wie Autoren und Coautoren.
     * 
     * Gibt ein fehlerhaftes Result zurück, wenn Autoren nicht geparst werden konnte,
     * bei Erfolg ein leeres Result.
     * 
     * Ein leeres Result zeigt an, dass kein Fehler aufgetreten ist.
     * 
     * @param buch das erfolgreich geparste Buch-Produkt
     * @param itemData die zugrundeliegenden Produktdaten
     * @return bei Erfolg leeres Result, sonst fehlerhaftes Result
     */
    private Result<Void> parseBuchData(Buch buch, ItemData itemData) {
        final List<AuthorData> authors = itemData.getAuthors();

        // Kein Autor sollte kein Problem sein
        if (authors == null || authors.isEmpty())
            return Result.empty();

        // Hauptautor (erster in der Liste)
        final String hauptautorName = authors.get(0).getName();
        final Result<Person> hauptautorResult = parsePerson(buch, hauptautorName);

        if (hauptautorResult.isError() || hauptautorResult.isEmpty()) {
            ImportStatistik.increment("[Book] Could not parse main author");
            return Result.error("Could not parse main author: " + hauptautorResult.getErrorMessage());
        }

        final Person hauptautor = hauptautorResult.getValue();

        // Hauptautor speichern
        Autoren hauptAutorenEintrag = new Autoren();
        hauptAutorenEintrag.setPerson(hauptautor);
        hauptAutorenEintrag.setProdukt(buch);
        // Hauptautor bleibt null
        autorenRepository.save(hauptAutorenEintrag);

        // Coautoren
        for (int i = 1; i < authors.size(); i++) {
            final String coName = authors.get(i).getName();
            final Result<Person> coResult = parsePerson(buch, coName);

            if (coResult.isError()) {
                ImportStatistik.increment("[Book] Could not parse co-author");
                return Result.error("Could not parse co-author: " + coResult.getErrorMessage());
            }

            if (coResult.isValid()) {
                final Autoren coautor = new Autoren();
                coautor.setPerson(coResult.getValue());
                coautor.setProdukt(buch);
                coautor.setHauptautor(hauptautor);
                autorenRepository.save(coautor);
            }
        }

        return Result.empty();
    }

}
