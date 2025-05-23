package com.backend.service.parser;

import java.time.LocalDate;
import org.springframework.stereotype.Service;

import com.backend.entity.DVD;
import com.backend.entity.DVDRollen;
import com.backend.entity.Person;
import com.backend.entity.Produkt;
import com.backend.repository.DVDRepository;
import com.backend.repository.DVDRollenRepository;
import com.backend.service.dto.ActorData;
import com.backend.service.dto.CreatorData;
import com.backend.service.dto.DVDSpecData;
import com.backend.service.dto.DirectorData;
import com.backend.service.dto.FormatData;
import com.backend.service.dto.ItemData;
import com.backend.service.util.ImportLogger;
import com.backend.service.util.ImportStatistik;
import com.backend.service.util.ParseUtil;
import com.backend.service.util.Result;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Klasse zum Importieren von Produkten des Typs "DVD"
@Slf4j
@RequiredArgsConstructor
@Service
public class DVDImportParser extends ProduktImportParser {

    private final DVDRepository dvdRepository;
    private final DVDRollenRepository dvdRollenRepository;

    /**
     * Überschriebende Methode zum Laden eines Produkts vom Typ "DVD".
     * 
     * Gibt ein fehlerhaftes Result zurück, wenn das Produkt oder DVD-bezogende
     * Daten nicht geparst werden konnten, und loggt den Fehler in Konsole und
     * Datei.
     */
    @Override
    public Result<? extends Produkt> parseProdukt(ItemData itemData) {
        final Result<DVD> dvd = parseDVD(itemData);

        if (dvd.isError()) {
            String msg = "DVD: " + dvd.getErrorMessage() + " [Ignored]";
            ImportLogger.logError("DVDImport", itemData, msg);
            return Result.error(msg);
        }

        final Result<Void> result = parseDVDData(dvd.getValue(), itemData);

        if (result.isError()) {
            String msg = "DVD-Data: " + result.getErrorMessage() + " [Ignored]";
            ImportLogger.logError("DVDDataImport", itemData, msg);
            return Result.error(msg);
        }

        return dvd;
    }

    /**
     * Parst das Produkt als DVD und überprüft syntaktische sowie semantische
     * Integritäten.
     * 
     * @param itemData die zugrundeliegenden Produktdaten
     * @return Result mit erfolgreich geparster DVD, sonst fehlerhaftes Result
     */
    private Result<DVD> parseDVD(ItemData itemData) {
        final DVDSpecData dvdSpecData = itemData.getDvdspec();

        // DVD hat keine DVDDaten
        if (dvdSpecData == null) {
            return Result.error("The provided dvd spec data is empty for DVD: " + itemData.getAsin());
        }

        final String asin = itemData.getAsin();
        final String title = itemData.getTitle();
        final String picture = itemData.getPicture();
        final String salesRank = itemData.getSalesrank();
        final String releaseDate = dvdSpecData.getReleasedate();
        final FormatData format = dvdSpecData.getFormat();

        Integer regionCode = dvdSpecData.getRegionCode();
        Integer laufzeit = dvdSpecData.getRunningTime();

        Integer parsedSalesRank = ParseUtil.parseInteger(salesRank);
        LocalDate parsedReleaseDate = ParseUtil.parseDate(releaseDate);
        final String parsedFormat = ParseUtil.parseFormat(format);

        if (asin == null || asin.isBlank()) {
            ImportStatistik.increment("[DVD] asin is null");
            return Result.error("asin is null.");
        }

        if (title == null || title.isBlank()) {
            ImportStatistik.increment("[DVD] title is null");
            return Result.error("title is null (" + itemData.getAsin() + ").");
        }

        // wenn vorhanden, aber negativ: Warn
        if (regionCode != null && regionCode < 0) {
            ImportStatistik.increment("[DVD] region-code is negative");
            String msg = "region-code is negative: (" + itemData.getAsin() + "). [Removed]";
            ImportLogger.logWarning("DVDImport", itemData, msg);
            log.warn(msg);
            regionCode = null;
        }

        // wenn vorhanden, aber negativ: Warn
        if (laufzeit != null && laufzeit < 0) {
            ImportStatistik.increment("[DVD] laufzeit is negative");
            String msg = "laufzeit is negative (" + itemData.getAsin() + "). [Removed]";
            ImportLogger.logWarning("DVDImport", itemData, msg);
            log.warn(msg);
            laufzeit = null;
        }

        // Salesrank konnte nicht zu Integer umgewandelt werden oder ist negativ
        if (salesRank != null && !salesRank.isBlank() && (parsedSalesRank == null || parsedSalesRank < 0)) {
            ImportStatistik.increment("[DVD] sales rank isnt integer or negative");
            String msg = "sales rank isnt integer or negative: " + salesRank + ". (" + itemData.getAsin()
                    + "). [Removed]";
            ImportLogger.logWarning("BookImport", itemData, msg);
            log.warn(msg);
            parsedSalesRank = null;
        }

        // Release-Date konnte nicht zu LocalDate umgewandelt werden
        if (releaseDate != null && !releaseDate.isBlank() && parsedReleaseDate == null) {
            ImportStatistik.increment("[DVD] release date isnt date");
            String msg = "release date isnt date: " + releaseDate + ". (" + itemData.getAsin() + "). [Removed]";
            ImportLogger.logWarning("BookImport", itemData, msg);
            log.warn(msg);
            parsedReleaseDate = null;
        }

        final DVD dvd = new DVD();

        dvd.setProduktId(asin);
        dvd.setTitel(title);
        dvd.setBild(picture);
        dvd.setVerkaufsrang(salesRank == null || salesRank.isBlank() ? null : parsedSalesRank);
        dvd.setErscheinungsdatum(releaseDate == null || releaseDate.isBlank() ? null : parsedReleaseDate);
        dvd.setRegionCode(regionCode);
        dvd.setFormat(parsedFormat);
        dvd.setLaufzeit(laufzeit);

        dvdRepository.save(dvd);
        return Result.of(dvd);
    }

    /**
     * Parst DVD-bezogen Daten wie Arist, Creator oder Director.
     * 
     * Gibt ein fehlerhaftes Result zurück, wenn eine Person nicht geparst werden
     * konnte,
     * bei Erfolg ein leeres Result.
     * 
     * @param dvd      das erfolgreich geparste DVD-Produkt
     * @param itemData die zugrundeliegenden Produktdaten
     * @return bei Erfolg leeres Result, sonst fehlerhaftes Result
     */
    private Result<Void> parseDVDData(DVD dvd, ItemData itemData) {

        // wenn Actors vorhanden sind
        if (itemData.getActors() != null) {
            for (final ActorData actor : itemData.getActors()) {
                final Result<Person> personResult = parsePerson(dvd, actor.getName()); // Parsen der Person
                if (personResult.isError()) {
                    return Result.error("Could not parse actor: " + personResult.getErrorMessage());
                }
                if (personResult.isValid() && personResult.getValue() != null) { // Erfolg und vorhandener Wert
                    saveDVDPersonRole(dvd, personResult.getValue(), "Actor"); // Speichern als Actor
                }
            }
        }

        if (itemData.getDirectors() != null) {
            for (final DirectorData director : itemData.getDirectors()) {
                final Result<Person> personResult = parsePerson(dvd, director.getName());
                if (personResult.isError()) {
                    return Result.error("Could not parse director: " + personResult.getErrorMessage());
                }
                if (personResult.isValid() && personResult.getValue() != null) {
                    saveDVDPersonRole(dvd, personResult.getValue(), "Director"); // Speichern als Director
                }
            }
        }

        if (itemData.getCreators() != null) {
            for (final CreatorData creator : itemData.getCreators()) {
                final Result<Person> personResult = parsePerson(dvd, creator.getName());
                if (personResult.isError()) {
                    return Result.error("Could not parse creator: " + personResult.getErrorMessage());
                }
                if (personResult.isValid() && personResult.getValue() != null) {
                    saveDVDPersonRole(dvd, personResult.getValue(), "Creator"); // Speichern als Creator
                }
            }
        }

        return Result.empty();
    }

    /**
     * Speichert die Person zu einer DVD mit der gegebenen Rolle.
     * 
     * @param dvd    die erfolgreich geparste DVD-Produkt
     * @param person die erfolgreich geparste Person
     * @param rolle die zur Person zuzuordnene Rolle
     * 
     * @throws IllegalStateException wenn DVD oder Person nicht korrekt gespeichert wurden
     */
    private void saveDVDPersonRole(DVD dvd, Person person, String rolle) {
        if (dvd == null || dvd.getProduktId() == null || person == null || person.getPersonId() == null) {
            throw new IllegalStateException("DVD oder Person sind nicht korrekt initialisiert oder persistiert.");
        }

        DVDRollen dvdRolle = new DVDRollen();
        dvdRolle.setDvd(dvd);
        dvdRolle.setPerson(person);
        dvdRolle.setRolle(rolle);
        dvdRollenRepository.save(dvdRolle);
    }

}