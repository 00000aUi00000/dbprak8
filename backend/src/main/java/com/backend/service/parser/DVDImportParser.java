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
import com.backend.service.util.ParseUtil;
import com.backend.service.util.Result;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DVDImportParser extends ProduktImportParser {

    private final DVDRepository dvdRepository;
    private final DVDRollenRepository dvdRollenRepository;

    @Override
    public Result<? extends Produkt> parseProdukt(ItemData itemData) {
         final Result<DVD> dvd = parseDVD(itemData);

        if (dvd.isError()) {
            return Result.error("Could not parse DVD: " + dvd.getErrorMessage());
        }

        final Result<Void> result = parseDVDData(dvd.getValue(), itemData);

        if (result.isError()) {
            return Result.error("Could not parse DVD-Data: " + result.getErrorMessage());
        }

        return dvd;       
    }

    private Result<DVD> parseDVD(ItemData itemData) {
        final DVDSpecData dvdSpecData = itemData.getDvdspec();

        if (dvdSpecData == null) {
            return Result.error("The provided dvd spec data is empty for DVD: " + itemData.getAsin());
        }

        final String asin = itemData.getAsin();
        final String title = itemData.getTitle();
        final String picture = itemData.getPicture();
        final String salesRank = itemData.getSalesrank();
        final String releaseDate = dvdSpecData.getReleasedate();
        final FormatData format = dvdSpecData.getFormat();
        final Integer regionCode = dvdSpecData.getRegionCode();
        final Integer laufzeit = dvdSpecData.getRunningTime();

        final Integer parsedSalesRank = ParseUtil.parseInteger(salesRank);
        final LocalDate parsedReleaseDate = ParseUtil.parseDate(releaseDate);
        final String parsedFormat = ParseUtil.parseFormat(format);

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

    private Result<Void> parseDVDData(DVD dvd, ItemData itemData) {
        if (itemData.getActors() != null) {
            for (final ActorData actor : itemData.getActors()) {
                final Result<Person> personResult = parsePerson(dvd, actor.getName());
                if (personResult.isError()) {
                    return Result.error("Could not parse actor: " + personResult.getErrorMessage());
                }
                if (personResult.isValid() && personResult.getValue() != null) {
                    saveDVDPersonRole(dvd, personResult.getValue(), "Actor");
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
                    saveDVDPersonRole(dvd, personResult.getValue(), "Director");
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
                    saveDVDPersonRole(dvd, personResult.getValue(), "Creator");
                }
            }
        }

        return Result.empty();
    }


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