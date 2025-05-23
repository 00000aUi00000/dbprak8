package com.backend.service.parser;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.entity.AehnlichZu;
import com.backend.entity.Produkt;
import com.backend.repository.AehnlichzuRepository;
import com.backend.repository.ProduktRepository;
import com.backend.service.dto.ItemData;
import com.backend.service.util.ImportLogger;
import com.backend.service.util.ImportStatistik;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

// Klasse zum Parsen ähnlicher Produkte
@Slf4j
@Service
public class SimilarProductParser {

    @Autowired
    private ProduktRepository produktRepository;
    @Autowired
    private AehnlichzuRepository aehnlichzuRepository;

    /**
     * Parst alle Ähnlichkeitsbeziehungen in den gegebenden Items.
     * 
     * Falls mehrere Shops geparst werden, sollte dies erst ausgeführt werden,
     * wenn alle Shop-Daten erfolgreich importiert wurden.
     * 
     * @param items alle vorher geparsten Items
     */
    @Transactional
    public void parseSimilarProducts(List<ItemData> items) {
        if (items.isEmpty()) { // Fehler-Logging wenn keine Items vorhanden
            String msg = "Keine Items für Produktähnlichkeiten gefunden.";
            ImportLogger.logError("SimilarProductImport", null, msg);
            log.error(msg);
            return;
        }

        for (final ItemData item : items) {
            parseSimilarProduct(item);
        }
    }

    /**
     * Parst die Ähnlichkeiten für die gegebenen Produktdaten.
     * 
     * Loggt Fehler bzgl. ähnlichen Produkten in Konsole und Datei.
     * 
     * @param itemData die Produktdaten mit Ähnlichkeitsbeziehungen
     */
    private void parseSimilarProduct(ItemData itemData) {
        // Abbruch, wenn null oder keine Ähnlichkeiten vorhanden
        if (itemData == null || itemData.getSimilars() == null) {
            return;
        }

        final String asin = itemData.getAsin();

        // Abbruch, wenn keine Produkt-ID vorhanden
        if (asin == null || asin.isBlank()) {
            return;
        }

        // Laden des Eintrags aus Datenbank
        final Optional<Produkt> produkt = produktRepository.findById(asin);

        // Abbruch, wenn Produkt nicht in Datenbank
        if (produkt.isEmpty()) {
            return;
        }

        for (final String similarAsin : itemData.getSimilars().getAsins()) {

            if (similarAsin == null || similarAsin.isBlank()) {
                continue;
            }

            // Laden des ähnlichen Produkts aus Datenbank
            final Optional<Produkt> similiarProdukt = produktRepository.findById(similarAsin);

            // Log bei nicht vorhandenem ähnlichen Produkt
            if (similiarProdukt.isEmpty()) {
                ImportStatistik.increment("[SimilarProduct] Similar product with ASIN not in database");
                String msg = "Similar product with ASIN " + similarAsin + " not in database. [Ignored]";
                ImportLogger.logWarning("SimilarProductImport", itemData, msg);
                log.warn(msg);
                continue;
            }

            // Log bei gleichem ähnlichen Produkt
            if (similiarProdukt.get().equals(produkt.get())) {
                ImportStatistik.increment("[SimilarProduct] Same products cannot be linked");
                String msg = "Same products cannot be linked (" + asin + " | " + similarAsin + "). [Ignored]";
                ImportLogger.logWarning("SimilarProductImport", itemData, msg);
                log.warn(msg);
                continue;
            }

            // Speichern der einseitige Beziehung
            final AehnlichZu aehnlichZu = new AehnlichZu();
            aehnlichZu.setProduktA(produkt.get());
            aehnlichZu.setProduktB(similiarProdukt.get());

            aehnlichzuRepository.save(aehnlichZu);
        }
        return;
    }

}
