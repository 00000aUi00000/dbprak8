package com.backend.service.parser;

import java.util.ArrayList;
import java.util.List;

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
import com.backend.service.util.ImportStatistik;
import com.backend.service.util.ParseUtil;
import com.backend.service.util.Result;

import lombok.extern.slf4j.Slf4j;

// abstrakte Klasse zum Importieren von Produkten mit vordefinierten Parse-Methoden
@Slf4j
public abstract class ProduktImportParser {

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private AngebotRepository angebotRepository;
    @Autowired
    private AngebotsdetailsRepository angebotsdetailsRepository;

    /**
     * Parst ein Produkt basierend auf der gegebenen ItemData.
     * 
     * @param itemData die Daten zum Parsen des Produkts
     * @return Result mit Subtype eines Produkts, ggf. leer oder fehlerhaft
     */
    public abstract Result<? extends Produkt> parseProdukt(ItemData itemData);

    /**
     * Parst eine Person, die dem gegebenen Produkt zugeordnet werden kann, und
     * einen gewissen Namen hat.
     * 
     * Gibt fehlerhaftes Result zurück, wenn der Name null ist oder ein leeres Result,
     * wenn der Name leer ist.
     * Ansonsten wird ggf. eine neue Person erstellt und zurückgegeben.
     * 
     * Gibt bereits vorhandene Person zurück, falls bereits eine gespeichert ist.
     * 
     * @param produkt das zuzuordnene Produkt
     * @param name    der Name der Person
     * @return Result mit Person, fehlerhaftes Result oder leeres Result
     */
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

    /**
     * Parst ein Angebot basierend auf der gegebenen Filiale und Produkt.
     * 
     * @param filiale die anbietende Filiale
     * @param produkt das angebotende Produkt
     * @return Result mit erstelltem Angebot
     */
    public Result<Angebot> parseAngebot(Filiale filiale, Produkt produkt) {
        final Angebot angebot = new Angebot();
        angebot.setFiliale(filiale);
        angebot.setProdukt(produkt);

        angebotRepository.save(angebot);
        return Result.of(angebot);
    }

    /**
     * Parst Angebotdetails basierend auf dem Angebot und den Produktdaten.
     * Falls der gegebene Preis negativ ist oder kein Multiplier vorhanden ist,
     * wird dies in der Konsole und einer Datei geloggt.
     * 
     * Gibt ein fehlerhaftes Result bei leerem Zustand zurück, ansonsten die
     * erstellten Angebotsdetails.
     * 
     * @param angebot  das zugrundeliegende Angebot
     * @param itemData die Produktdaten zum Parsen
     * @return Result mit angebotsdetails oder fehlerhaftes Result bei leerem
     *         Zustand
     */
    public Result<List<Angebotsdetails>> parseAngebotdetails(Angebot angebot, ItemData itemData) {
        final List<Angebotsdetails> result = new ArrayList<>();

        for (final PriceData priceData : itemData.getPrices()) {
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
                ImportStatistik.increment("[Product] price is negative");
                String msg = "price is negative: (" + itemData.getAsin() + "). [Removed]";
                ImportLogger.logWarning("ProduktImport", itemData, msg);
                log.warn(msg);
                angebotsDetails.setPreis(null);
            } else {

                if (multiplier == null) {
                    ImportStatistik.increment("[Product] multiplier is null");
                    String msg = "multiplier is null: (" + itemData.getAsin() + "). [Using 0.01]";
                    ImportLogger.logWarning("ProduktImport", itemData, msg);
                    log.warn(msg);
                }

                multiplier = (multiplier == null) ? 0.01 : multiplier;
                angebotsDetails.setPreis(price != null ? (price * multiplier) : null);
            }

            angebotsdetailsRepository.save(angebotsDetails);
            result.add(angebotsDetails);
        }

        return Result.of(result);
    }

}