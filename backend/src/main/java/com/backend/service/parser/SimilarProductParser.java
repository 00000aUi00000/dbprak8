package com.backend.service.parser;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.entity.AehnlichZu;
import com.backend.entity.Produkt;
import com.backend.repository.AehnlichzuRepository;
import com.backend.repository.ProduktRepository;
import com.backend.service.dto.ItemData;
import com.backend.service.util.ImportLogger;
import com.backend.service.util.Result;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SimilarProductParser {

    @Autowired
    private ProduktRepository produktRepository;
    @Autowired
    private AehnlichzuRepository aehnlichzuRepository;

    @Transactional
    public void parseSimilarProducts(List<ItemData> items) {
        if (items.isEmpty()) {
            log.error("Keine Items für Produktähnlichkeiten gefunden.");
            return;
        }

        for (final ItemData item : items) {
            parseSimilarProduct(item, items);
        }

    }

    private Result<Void> parseSimilarProduct(ItemData itemData, List<ItemData> allItems) {
        if (itemData == null || itemData.getSimilars() == null) {
            return Result.empty();
        }

        final String asin = itemData.getAsin();

        if (asin == null || asin.isBlank()) {
            return Result.empty();
        }

        final Optional<Produkt> produkt = produktRepository.findById(asin);

        if (produkt.isEmpty()) {
            return Result.empty();
        }

        for (final String similarAsin : itemData.getSimilars().getAsins()) {

            if (similarAsin == null || similarAsin.isBlank()) {
                continue;
            }

            final Optional<Produkt> similiarProdukt = produktRepository.findById(similarAsin);

            if (similiarProdukt.isEmpty()) {
                String msg = "Similar product with ASIN " + similarAsin + " not in database. Ignoring.";
                ImportLogger.logWarning("SimilarProductImport", itemData, msg);
                log.warn(msg);
                continue;
            }

            if (similiarProdukt.get().equals(produkt.get())) {
                String msg = "Same products cannot be linked (" + asin + " | " + similarAsin + "). Ignoring.";
                ImportLogger.logWarning("SimilarProductImport", itemData, msg);
                log.warn(msg);
                continue;
            }

            final Optional<ItemData> similarProduktItemData = findItemDataOf(similarAsin, allItems);
            final boolean noBackreference = similarProduktItemData.isEmpty()
                    || similarProduktItemData.get().getSimilars() == null
                    || similarProduktItemData.get().getSimilars().getAsins().stream().noneMatch(it -> asin.equals(it));

            if (noBackreference) {
                String msg = "Similar product " + similarAsin + " has no backreference to product " + asin
                        + ". Setting link manually.";
                ImportLogger.logWarning("SimilarProductImport", itemData, msg);
                log.warn(msg);
            }

            final AehnlichZu aehnlichZu = new AehnlichZu();
            aehnlichZu.setProduktA(produkt.get());
            aehnlichZu.setProduktB(similiarProdukt.get());

            final AehnlichZu aehnlichZu2 = new AehnlichZu();
            aehnlichZu2.setProduktA(similiarProdukt.get());
            aehnlichZu2.setProduktB(produkt.get());

            aehnlichzuRepository.save(aehnlichZu);
            aehnlichzuRepository.save(aehnlichZu2);
        }
        return Result.empty();
    }

    private Optional<ItemData> findItemDataOf(String asin, List<ItemData> items) {
        return items.stream()
                .filter(Objects::nonNull)
                .filter(it -> asin.equals(it.getAsin()))
                .findAny();
    }

}
