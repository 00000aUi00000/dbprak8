package com.backend.service.parser;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.entity.Kuenstler;
import com.backend.entity.Labelliste;
import com.backend.entity.MusikCD;
import com.backend.entity.Person;
import com.backend.entity.Produkt;
import com.backend.entity.Trackliste;
import com.backend.repository.KuenstlerRepository;
import com.backend.repository.LabellisteRepository;
import com.backend.repository.MusikCDRepository;
import com.backend.repository.TracklisteRepository;
import com.backend.service.dto.ArtistData;
import com.backend.service.dto.CreatorData;
import com.backend.service.dto.ItemData;
import com.backend.service.dto.LabelData;
import com.backend.service.dto.MusicSpecData;
import com.backend.service.util.ImportLogger;
import com.backend.service.util.ParseUtil;
import com.backend.service.util.Result;

import lombok.extern.slf4j.Slf4j;

// Klasse zum Importieren von Produkten des Typs "MusikCD"
@Slf4j
@Service
public class MusikCDImportParser extends ProduktImportParser {

    @Autowired
    private MusikCDRepository musikCDRepository;
    @Autowired
    private LabellisteRepository labellisteRepository;
    @Autowired
    private TracklisteRepository tracklisteRepository;
    @Autowired
    private KuenstlerRepository kuenstlerRepository;

    /**
     * Überschriebende Methode zum Laden eines Produkts vom Typ "MusikCD".
     * 
     * Gibt ein fehlerhaftes Result zurück, wenn das Produkt oder Musik-bezogende
     * Daten nicht geparst werden konnten, und loggt den Fehler in Konsole und
     * Datei.
     */
    @Override
    public Result<? extends Produkt> parseProdukt(ItemData itemData) {
        final Result<MusikCD> musikCD = parseMusikCD(itemData);

        if (musikCD.isError()) {
            String msg = "MusikCD: " + musikCD.getErrorMessage() + " [Ignored]";
            ImportLogger.logError("MusikCDImport", itemData, msg);
            return Result.error(msg);
        }

        final Result<Void> result = parseMusicData(musikCD.getValue(), itemData);

        if (result.isError()) {
            String msg = "MusikCD-Data: " + result.getErrorMessage() + " [Ignored]";
            ImportLogger.logError("MusikCDDataImport", itemData, msg);
            return Result.error(msg);
        }

        return musikCD;
    }

     /**
     * Parst das Produkt als MusikCD und überprüft syntaktische sowie semantische
     * Integritäten.
     * 
     * @param itemData die zugrundeliegenden Produktdaten
     * @return Result mit erfolgreich geparster MusikCD, sonst fehlerhaftes Result
     */
    private Result<MusikCD> parseMusikCD(ItemData itemData) {
        final MusicSpecData musicSpecData = itemData.getMusicspec();
        
        // MusikCD hat keine Musikdaten
        if (musicSpecData == null) {
            return Result.error("The provided music spec data is empty for MusikCD: " + itemData.getAsin());
        }

        final String asin = itemData.getAsin();
        final String title = itemData.getTitle();
        final String picture = itemData.getPicture();
        final String salesRank = itemData.getSalesrank();
        final String releaseDate = musicSpecData.getReleaseDate();

        Integer numDisk = musicSpecData.getNumDiscs();
        Integer parsedSalesRank = ParseUtil.parseInteger(salesRank);
        LocalDate parsedReleaseDate = ParseUtil.parseDate(releaseDate);

        if (asin == null || asin.isBlank()) {
            return Result.error("asin is null.");
        }

        if (title == null || title.isBlank()) {
            return Result.error("title is null (" + itemData.getAsin() + ").");
        }

        // Salesrank konnte nicht zu Integer umgewandelt werden oder ist negativ
        if (salesRank != null && !salesRank.isBlank() && (parsedSalesRank == null || parsedSalesRank < 0)) {
            String msg = "sales rank isnt integer or negative: " + salesRank + ". (" + itemData.getAsin() + "). [Removed]";
            ImportLogger.logWarning("BookImport", itemData, msg);
            log.warn(msg);
            parsedSalesRank = null;
        }

        // Release-Date konnte nicht zu LocalDate umgewandelt werden
        if (releaseDate != null && !releaseDate.isBlank() && parsedReleaseDate == null) {
            String msg = "release date isnt date: " + releaseDate + ". (" + itemData.getAsin() + "). [Removed]";
            ImportLogger.logWarning("BookImport", itemData, msg);
            log.warn(msg);
            parsedReleaseDate = null;
        }

        // Default bei nicht-vorhandener Diskanzahl
        if (numDisk == null) {
            numDisk = 1;
        }

        final MusikCD musikCD = new MusikCD();

        musikCD.setProduktId(asin);
        musikCD.setTitel(title);
        musikCD.setBild(picture);
        musikCD.setAnzahlcds(numDisk);
        musikCD.setVerkaufsrang(salesRank == null || salesRank.isBlank() ? null : parsedSalesRank); // wenn Salesrank vorhanden, nutze geparsten Salesrank
        musikCD.setErscheinungsdatum(releaseDate == null || releaseDate.isBlank() ? null : parsedReleaseDate); // wenn Publication vorhanden, nutze geparsten Publication

        musikCDRepository.save(musikCD);

        return Result.of(musikCD);
    }

    /**
     * Parst Musik-bezogen Daten wie Künstler, Labelliste und Trackliste.
     * 
     * Gibt ein fehlerhaftes Result zurück, wenn keine Künstler vorhanden sind,
     * bei Erfolg ein leeres Result.
     * 
     * Ein leeres Result zeigt an, dass kein Fehler aufgetreten ist.
     * 
     * @param musikCD das erfolgreich geparste MusikCD-Produkt
     * @param itemData die zugrundeliegenden Produktdaten
     * @return bei Erfolg leeres Result, sonst fehlerhaftes Result
     */
    private Result<Void> parseMusicData(MusikCD musikCD, ItemData itemData) {

        // Prüfung auf Artist-Data
        boolean atleastOneArtist = false;

        for (final ArtistData artistData : itemData.getArtists()) {
            final Result<Person> person = parsePerson(musikCD, artistData.getName());

            if (person.isError()) {
                return Result.error("Could not parse Artist: " + person.getErrorMessage());
            }

            if (person.isValid()) {
                final Result<Kuenstler> kuenstler = parseKuenstler(musikCD, person.getValue());
                musikCD.addKuenstler(kuenstler.getValue());

                atleastOneArtist = true;
            }
        }

        // Prüfung auf Creator-Data
        boolean atleastOneCreator = false;

        for (final CreatorData creatorData : itemData.getCreators()) {
            final Result<Person> person = parsePerson(musikCD, creatorData.getName());

            if (person.isError()) {
                return Result.error("Could not parse creator: " + person.getErrorMessage());
            }

            if (person.isValid()) {
                final Result<Kuenstler> kuenstler = parseKuenstler(musikCD, person.getValue());
                musikCD.addKuenstler(kuenstler.getValue());

                atleastOneCreator = true;
            }
        }

        // wenn kein Artist / Creator vorhanden ist, soll die MusikCD gelöscht und ein Fehler ausgegeben werden
        if (!atleastOneArtist && !atleastOneCreator) {
            musikCDRepository.delete(musikCD);
            return Result.error("No artist or creator found for MusikCD " + musikCD.getProduktId());
        }

        for (final LabelData labelData : itemData.getLabels()) {
            final Result<Labelliste> labelliste = parseLabelliste(musikCD, labelData);

            if (labelliste.isError()) {
                return Result.error("Could not parse labelliste: " + labelliste.getErrorMessage());
            }

            if (labelliste.isValid()) {
                musikCD.addLabelliste(labelliste.getValue());
            }
        }

        for (String track : itemData.getTracks()) {
            final Result<Trackliste> trackliste = parseTrackliste(musikCD, track);

            if (trackliste.isError()) {
                return Result.error("Could not parse trackliste: " + trackliste.getErrorMessage());
            }

            if (trackliste.isValid()) {
                musikCD.addTrackliste(trackliste.getValue());
            }
        }

        return Result.empty();
    }

    /**
     * Parst die Labelliste für die gegebende MusikCD mit den Label-Daten.
     * 
     * Gibt ein fehlerhaftes Result zurück, wenn der Name null ist oder 
     * ein leeres Result, wenn der Name leer ist.
     * 
     * Ansonsten wird die geparste und gespeicherte Labelliste zurückgegeben.
     * 
     * @param musikCD die zugehörige MusikCD
     * @param labelData die zugrundeliegenden Daten zum Parsen
     * @return Result mit Labelliste bei Erfolg, leeres oder fehlerhaftes Result
     */
    private Result<Labelliste> parseLabelliste(MusikCD musikCD, LabelData labelData) {
        final String name = labelData.getName();

        if (name == null) {
            return Result.error("The label name of the label data is null (" + musikCD.getProduktId() + ").");
        }

        if (name.isBlank()) {
            return Result.empty(); // bei labeln die leer sind, soll nichts weiter passieren
        }

        final Labelliste labelliste = new Labelliste();
        labelliste.setLabel(name);
        labelliste.setMusikCD(musikCD);

        labellisteRepository.save(labelliste);
        return Result.of(labelliste);
    }
    
    /**
     * Parst die Trackliste für die gegebende MusikCD mit dem Tracknamen.
     * 
     * Gibt ein fehlerhaftes Result zurück, wenn der Name null ist oder 
     * ein leeres Result, wenn der Name leer ist.
     * 
     * Ansonsten wird die geparste und gespeicherte Trackliste zurückgegeben.
     * 
     * @param musikCD die zugehörige MusikCD
     * @param track der Name des Tracks
     * @return Result mit Trackliste bei Erfolg, leeres oder fehlerhaftes Result
     */
    private Result<Trackliste> parseTrackliste(MusikCD musikCD, String track) {
        if (track == null) {
            return Result.error("The track of the MusikCD is null (" + musikCD.getProduktId() + ").");
        }

        if (track.isBlank()) {
            return Result.empty(); // bei track namen die leer sind, soll nichts weiter passieren
        }

        final Trackliste trackliste = new Trackliste();
        trackliste.setTitel(track);
        trackliste.setMusikCD(musikCD);

        tracklisteRepository.save(trackliste);
        return Result.of(trackliste);
    }

    /**
     * Parst einen Künstler für die gegebende MusikCD und Person.
     * 
     * @param musikCD die zugehörige MusikCD
     * @param person die Person für den Künstler
     * @return Result mit Künstler bei Erfolg, leeres oder fehlerhaftes Result
     */
    private Result<Kuenstler> parseKuenstler(MusikCD musikCD, Person person) {
        final Kuenstler kuenstler = new Kuenstler();
        kuenstler.setMusikCD(musikCD);
        kuenstler.setPerson(person);

        kuenstlerRepository.save(kuenstler);
        return Result.of(kuenstler);
    }

}