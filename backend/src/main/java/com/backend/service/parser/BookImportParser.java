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
import com.backend.service.util.ParseUtil;
import com.backend.service.util.Result;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BookImportParser extends ProduktImportParser {

    private final BuchRepository buchRepository;
    private final AutorenRepository autorenRepository;

    @Override
    public Result<? extends Produkt> parseProdukt(ItemData itemData) {
        final Result<Buch> buch = parseBuch(itemData);

        if (buch.isError()) {
            ImportLogger.logError("BookImport", itemData, buch.getErrorMessage());
            return Result.error("Could not parse Buch: " + buch.getErrorMessage());
        }

        final Result<Void> result = parseBuchData(buch.getValue(), itemData);

        if (result.isError()) {
            ImportLogger.logError("BookDataImport", itemData, result.getErrorMessage());
            return Result.error("Could not parse Buch-Data: " + result.getErrorMessage());
        }

        return buch;
    }

    private Result<Buch> parseBuch(ItemData itemData) {
        final BookSpecData bookSpecData = itemData.getBookspec();

        if (bookSpecData == null) {
            return Result.error("The provided book spec data is empty for Buch: " + itemData.getAsin());
        }

        final String asin = itemData.getAsin();
        final String title = itemData.getTitle();
        final String picture = itemData.getPicture();
        final String salesRank = itemData.getSalesrank();
        final String publication = bookSpecData.getPublication() != null ? bookSpecData.getPublication().getValue() : null;
        final ISBNData isbn = bookSpecData.getIsbnData();
        final List<PublisherData> publisher = itemData.getPublisher();
        //final PublisherData publisherData = publisher != null && !publisher.isEmpty() ? publisher.get(0) : null;
        final int seitenZahl = bookSpecData.getPages();

        final Integer parsedSalesRank = ParseUtil.parseInteger(salesRank);
        final LocalDate parsedPublication = ParseUtil.parseDate(publication);

        if (asin == null || asin.isBlank()) {
            return Result.error("The asin of the given item is null.");
        }

        if (title == null || title.isBlank()) {
            return Result.error("The title of the given item is null (" + itemData.getAsin() + ").");
        }

        // if (isbn == null || isbn.getValue() == null || isbn.getValue().isBlank()) {
        //     return Result.error("The isbn of the given item is null (" + itemData.getAsin() + ").");
        // }

        // TODO Publisher kann null sein, bei mehr als einem werden die Datensätze zu einem zusammengefügt. Klärung ob das beibehalten werden soll
        // if (publisher != null && publisher.size() > 1) {
        //     return Result.error("The item has more than one publisher (" + itemData.getAsin() + ").");
        // }

        if (salesRank != null && !salesRank.isBlank() && parsedSalesRank == null) {
            return Result.error("The sales rank of the given item is not an integer: " + salesRank + ". ("
                    + itemData.getAsin() + ").");
        }

        if (publication != null && !publication.isBlank() && parsedPublication == null) {
            return Result.error("The release date of the given item is not a date: " + publication + ". ("
                    + itemData.getAsin() + ").");
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
        buch.setVerkaufsrang(salesRank == null || salesRank.isBlank() ? null : parsedSalesRank);
        buch.setErscheinungsdatum(publication == null || publication.isBlank() ? null : parsedPublication);
        buch.setIsbn(isbn != null ? isbn.getValue() : null); // Einträge ohne ISBN sollen möglich sein
        buch.setSeitenanzahl(seitenZahl);
        //buch.setVerlag(publisherData != null ? publisherData.getName() : null); // TODO: Sollte kein Verlag erlaubt sein? <- Aus meiner Sicht wäre es ok
        buch.setVerlag(verlagKombiniert);

        buchRepository.save(buch);

        return Result.of(buch);
    }

    private Result<Void> parseBuchData(Buch buch, ItemData itemData) {
        final List<AuthorData> authors = itemData.getAuthors();

        // TODO Kein Autor sollte kein Problem sein
        // if (authors == null || authors.isEmpty()) {
        //     return Result.error("No authors found for book " + buch.getProduktId());
        // }

        if (authors == null || authors.isEmpty()) return Result.empty();

        // Hauptautor (erster in der Liste)
        final String hauptautorName = authors.get(0).getName();
        final Result<Person> hauptautorResult = parsePerson(buch, hauptautorName);

        if (hauptautorResult.isError() || hauptautorResult.isEmpty()) {
            return Result.error("Could not parse main author: " + hauptautorResult.getErrorMessage());
        }

        final Person hauptautor = hauptautorResult.getValue();

        // Hauptautor speichern
        Autoren hauptAutorenEintrag = new Autoren();
        hauptAutorenEintrag.setPerson(hauptautor);
        hauptAutorenEintrag.setProdukt(buch);
        // hauptautor bleibt null
        autorenRepository.save(hauptAutorenEintrag);

        // Coautoren
        for (int i = 1; i < authors.size(); i++) {
            final String coName = authors.get(i).getName();
            final Result<Person> coResult = parsePerson(buch, coName);

            if (coResult.isError()) {
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
