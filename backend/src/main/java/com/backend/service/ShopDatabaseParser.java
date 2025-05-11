package com.backend.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.backend.entity.Angebot;
import com.backend.entity.Angebotsdetails;
import com.backend.entity.Filiale;
import com.backend.entity.Kuenstler;
import com.backend.entity.Labelliste;
import com.backend.entity.MusikCD;
import com.backend.entity.Person;
import com.backend.entity.Produkt;
import com.backend.entity.Trackliste;
import com.backend.repository.AngebotRepository;
import com.backend.repository.AngebotsdetailsRepository;
import com.backend.repository.FilialeRepository;
import com.backend.repository.KuenstlerRepository;
import com.backend.repository.LabellisteRepository;
import com.backend.repository.MusikCDRepository;
import com.backend.repository.PersonRepository;
import com.backend.repository.TracklisteRepository;
import com.backend.service.dto.ArtistData;
import com.backend.service.dto.ItemData;
import com.backend.service.dto.LabelData;
import com.backend.service.dto.MusicSpecData;
import com.backend.service.dto.PriceData;
import com.backend.service.dto.ShopData;
import com.backend.service.util.Result;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import com.backend.service.util.ParseUtil;

@Slf4j
@Component
public class ShopDatabaseParser {

    @Autowired
    private FilialeRepository filialeRepository;
    @Autowired
    private AngebotRepository angebotRepository;
    @Autowired
    private AngebotsdetailsRepository angebotsdetailsRepository;
    @Autowired
    private MusikCDRepository musikCDRepository;
    @Autowired
    private KuenstlerRepository kuenstlerRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private LabellisteRepository labellisteRepository;
    @Autowired
    private TracklisteRepository tracklisteRepository;

    public void parseData(final ShopData shopData) {
        final Result<Filiale> filiale = parseFiliale(shopData);

        if (filiale.isError()) {
            log.error("Could not parse filiale: " + filiale.getErrorMessage());
            return;
        }

        for (final ItemData itemData : shopData.getItems()) {
            final Result<Void> result = parseItemData(filiale.getValue(), itemData);

            if (result.isError()) {
                // anstatt zu loggen, könnte man ie Fehler an eine extra Klasse (z.B. ErrorReport) hinzufügen
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

        final Filiale filiale = new Filiale();
        filiale.setName(name);
        filiale.setAnschrift(street);

        filialeRepository.save(filiale);
        return Result.of(filiale);
    }

    @Transactional
    private Result<Void> parseItemData(Filiale filiale, ItemData itemData) {

        if (itemData == null) {
            return Result.error("The provided item data is null.");
        }

        if (itemData.getPgroup() == null) {
            return Result.error("The group of the provided item is null.");
        }

        // hole Produkt für entsprechende Gruppe und führe individuelle Speicherschritte aus
        final Result<? extends Produkt> produkt = switch (itemData.getPgroup()) {
            case "Music" -> {
                final Result<MusikCD> musikCD = parseMusikCD(itemData);

                if (musikCD.isError()) {
                    yield Result.error("Could not parse MusikCD: " + musikCD.getErrorMessage());
                }

                final Result<Void> result = parseMusicData(musikCD.getValue(), itemData);

                if (result.isError()) {
                    yield Result.error("Could not parse MusikCD-Data: " + result.getErrorMessage());
                }

                yield musikCD;
            }
            case "Book" -> {
                // TBD
                yield null;
            }
            case "DVD" -> {
               // TBD
                yield null;
            }
            default -> {
                yield Result.error("The product type " + itemData.getPgroup() + " is not registered.");
            }
        };

        // temporär für TDB DVD / Book
        if (produkt == null) {
            return Result.empty();
        }

        // wenn es einen Fehler bei der Produkterstellung gab, breche ab und leite diesen weiter
        if (produkt.isError()) {
            return Result.error(produkt.getErrorMessage());
        }

        final Result<Angebot> angebot = parseAngebot(filiale, produkt.getValue());
        final Result<Angebotsdetails> angebotdetails = parseAngebotdetails(angebot.getValue(), itemData);

        filiale.getAngebote().add(angebot.getValue());

        if (angebotdetails.isError()) {
            return Result.error("Could not parse angebot details: " + angebotdetails.getErrorMessage());
        }

        return Result.empty();
    }

    private Result<Void> parseMusicData(MusikCD musikCD, ItemData itemData) {

        for (final LabelData labelData : itemData.getLabels()) {
            final Result<Labelliste> labelliste = parseLabelliste(musikCD, labelData);

            if (labelliste.isError()) {
                return Result.error("Could not parse labelliste: " + labelliste.getErrorMessage());
            }

            if (labelliste.isValid()) {
                musikCD.getLabelliste().add(labelliste.getValue());
            }

        }

        for (final ArtistData artistData : itemData.getArtists()) {
            final Result<Person> person = parsePerson(musikCD, artistData.getName());

            if (person.isError()) {
                return Result.error("Could not parse person: " + person.getErrorMessage());
            }

            if (person.isValid()) {
                final Result<Kuenstler> kuenstler = parseKuenstler(musikCD, person.getValue());
                musikCD.getKuenstler().add(kuenstler.getValue());
            }
        }

        for (String track : itemData.getTracks()) {
            final Result<Trackliste> trackliste = parseTrackliste(musikCD, track);

            if (trackliste.isError()) {
                return Result.error("Could not parse trackliste: " + trackliste.getErrorMessage());
            }

            if (trackliste.isValid()) {
                musikCD.getTrackliste().add(trackliste.getValue());
            }

        }

        return Result.empty();
    }

    private void parseBookData(ItemData itemData) {
        // TBD
    }

    private void parseDVDData(ItemData itemData) {
        // TBD
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

        if (asin == null) {
            return Result.error("The asin of the given item is null.");
        }

        if (title == null) {
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

    private Result<Angebot> parseAngebot(Filiale filiale, Produkt produkt) {
        final Angebot angebot = new Angebot();
        angebot.setFiliale(filiale);
        angebot.setProdukt(produkt);

        angebotRepository.save(angebot);
        return Result.of(angebot);
    }

    private Result<Angebotsdetails> parseAngebotdetails(Angebot angebot, ItemData itemData) {
        final PriceData priceData = itemData.getPrice();
        final String state = priceData.getState();
        final Double price = priceData.getValue();

        if (state == null) {
            return Result.error("The state of the given item is null (" + itemData.getAsin() + ").");
        }

        final Angebotsdetails angebotsDetails = new Angebotsdetails();
        angebotsDetails.setAngebot(angebot);
        angebotsDetails.setZustand(state);
        angebotsDetails.setPreis(price);

        angebotsdetailsRepository.save(angebotsDetails);
        return Result.of(angebotsDetails);
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

    private Result<Person> parsePerson(MusikCD musikCD, String name) {
        if (name == null) {
            return Result.error("The name of the person is null (" + musikCD.getProduktId() + ").");
        }

        if (name.isBlank()) {
            return Result.empty();
        }

        final Person person = new Person();
        person.setName(name);

        personRepository.save(person);
        return Result.of(person);
    }

    private Result<Kuenstler> parseKuenstler(MusikCD musikCD, Person person) {
        final Kuenstler kuenstler = new Kuenstler();
        kuenstler.setMusikCD(musikCD);
        kuenstler.setPerson(person);

        kuenstlerRepository.save(kuenstler);
        return Result.of(kuenstler);
    }

}