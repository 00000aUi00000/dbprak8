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

    @Override
    public Result<? extends Produkt> parseProdukt(ItemData itemData) {
        final Result<MusikCD> musikCD = parseMusikCD(itemData);

        if (musikCD.isError()) {
            ImportLogger.logError("MusikCDImport", itemData, musikCD.getErrorMessage());
            return Result.error("Could not parse MusikCD: " + musikCD.getErrorMessage());
        }

        final Result<Void> result = parseMusicData(musikCD.getValue(), itemData);

        if (result.isError()) {
            ImportLogger.logError("MusikCDDataImport", itemData, musikCD.getErrorMessage());
            return Result.error("Could not parse MusikCD-Data: " + result.getErrorMessage());
        }

        return musikCD;
    }

    private Result<Void> parseMusicData(MusikCD musikCD, ItemData itemData) {

        for (final LabelData labelData : itemData.getLabels()) {
            final Result<Labelliste> labelliste = parseLabelliste(musikCD, labelData);

            if (labelliste.isError()) {
                return Result.error("Could not parse labelliste: " + labelliste.getErrorMessage());
            }

            if (labelliste.isValid()) {
                musikCD.addLabelliste(labelliste.getValue());
            }

        }

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

        // TODO Brauchen wir wirklich zwingend einen von beiden Einträge? Oder eher optional?
        // Ggf. Rückgabe anpassen damit Datensatz geschrieben werden kann, auch wenn nicht vollständig
        // if (!atleastOneArtist && !atleastOneCreator) {
        //     return Result.error("No artist or creator found for MusikCD " + musikCD.getProduktId());
        // }


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

    private Result<MusikCD> parseMusikCD(ItemData itemData) {
        final MusicSpecData musicSpecData = itemData.getMusicspec();

        if (musicSpecData == null) {
            return Result.error("The provided music spec data is empty for MusikCD: " + itemData.getAsin());
        }

        // TBD: check if somehow malformed (i.e. dvdspec specified etc., creator,
        // specified, atleast one artist)

        final String asin = itemData.getAsin();
        final String title = itemData.getTitle();
        final String picture = itemData.getPicture();
        final String salesRank = itemData.getSalesrank();
        final String releaseDate = musicSpecData.getReleaseDate();

        final Integer parsedSalesRank = ParseUtil.parseInteger(salesRank);
        final LocalDate parsedReleaseDate = ParseUtil.parseDate(releaseDate);

        if (asin == null || asin.isBlank()) {
            return Result.error("The asin of the given item is null.");
        }

        if (title == null || title.isBlank()) {
            return Result.error("The title of the given item is null (" + itemData.getAsin() + ").");
        }

        if (salesRank != null && !salesRank.isBlank() && parsedSalesRank == null) {
            return Result.error("The sales rank of the given item is not an integer: " + salesRank + ". ("
                    + itemData.getAsin() + ").");
        }

        if (releaseDate != null && !releaseDate.isBlank() && parsedReleaseDate == null) {
            return Result.error("The release date of the given item is not a date: " + releaseDate + ". ("
                    + itemData.getAsin() + ").");
        }

        final MusikCD musikCD = new MusikCD();

        musikCD.setProduktId(asin);
        musikCD.setTitel(title);
        musikCD.setBild(picture);
        musikCD.setVerkaufsrang(salesRank == null || salesRank.isBlank() ? null : parsedSalesRank);
        musikCD.setErscheinungsdatum(releaseDate == null || releaseDate.isBlank() ? null : parsedReleaseDate);

        musikCDRepository.save(musikCD);

        return Result.of(musikCD);
    }

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

    private Result<Kuenstler> parseKuenstler(MusikCD musikCD, Person person) {
        final Kuenstler kuenstler = new Kuenstler();
        kuenstler.setMusikCD(musikCD);
        kuenstler.setPerson(person);

        kuenstlerRepository.save(kuenstler);
        return Result.of(kuenstler);
    }

}