package com.backend.service.parser;

import org.springframework.beans.factory.annotation.Autowired;

import com.backend.entity.Angebot;
import com.backend.entity.Angebotsdetails;
import com.backend.entity.Filiale;
import com.backend.entity.Person;
import com.backend.entity.Produkt;
import com.backend.repository.AngebotRepository;
import com.backend.repository.AngebotsdetailsRepository;
import com.backend.repository.PersonRepository;
import com.backend.service.dto.ItemData;
import com.backend.service.dto.PriceData;
import com.backend.service.util.ImportLogger;
import com.backend.service.util.ParseUtil;
import com.backend.service.util.Result;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ProduktImportParser {

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private AngebotRepository angebotRepository;
    @Autowired
    private AngebotsdetailsRepository angebotsdetailsRepository;

    public abstract Result<? extends Produkt> parseProdukt(ItemData itemData);

    public Result<Person> parsePerson(Produkt produkt, String name) {
        if (name == null) {
            return Result.error("personname is null (" + produkt.getProduktId() + ").");
        }

        if (name.isBlank()) {
            return Result.empty();
        }

        return personRepository.findByName(name)
                .map(Result::of)
                .orElseGet(() -> {
                    Person person = new Person();
                    person.setName(name);
                    personRepository.save(person);
                    return Result.of(person);
                });
    }

    public Result<Angebot> parseAngebot(Filiale filiale, Produkt produkt) {
        final Angebot angebot = new Angebot();
        angebot.setFiliale(filiale);
        angebot.setProdukt(produkt);

        angebotRepository.save(angebot);
        return Result.of(angebot);
    }

    public Result<Angebotsdetails> parseAngebotdetails(Angebot angebot, ItemData itemData) {
        final PriceData priceData = itemData.getPrice();
        final String state = priceData.getState();
        final Double price = priceData.getValue();

        Double multiplier = ParseUtil.parseDouble(priceData.getMult());

        if (state == null) {
            return Result.error("state is null: (" + itemData.getAsin() + ").");
        }

        final Angebotsdetails angebotsDetails = new Angebotsdetails();
        angebotsDetails.setAngebot(angebot);
        angebotsDetails.setZustand(state);

        // Wenn Preis negativ, Preis wird er entfernt und Error geloggt
        if (price != null && price < 0.0) {
            String msg = "price is negative: (" + itemData.getAsin() + "). [Removed]";
            ImportLogger.logWarning("ProduktImport", itemData, msg);
            log.warn(msg);
            angebotsDetails.setPreis(null);
        } else {

            if (multiplier == null) {
                String msg = "multiplier is null: (" + itemData.getAsin() + "). [Using 0.01]";
                ImportLogger.logWarning("ProduktImport", itemData, msg);
                log.warn(msg);
            }

            multiplier = (multiplier == null) ? 0.01 : multiplier;
            angebotsDetails.setPreis(price != null ? (price * multiplier) : null);
        }

        angebotsdetailsRepository.save(angebotsDetails);
        return Result.of(angebotsDetails);
    }

}