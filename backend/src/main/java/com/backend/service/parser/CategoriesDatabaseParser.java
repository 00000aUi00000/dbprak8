package com.backend.service.parser;

import com.backend.entity.Kategorie;
import com.backend.entity.Produkt;
import com.backend.repository.KategorieRepository;
import com.backend.repository.ProduktRepository;
import com.backend.service.dto.CategoryData;
import com.backend.service.util.ImportLogger;
import com.backend.service.util.ImportStatistik;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

// Klasse zum Parsen von Kategorien
@RequiredArgsConstructor
@Slf4j
@Service
public class CategoriesDatabaseParser {

    private final KategorieRepository kategorieRepository;
    private final ProduktRepository produktRepository;

    private final Map<String, Kategorie> kategorienCache = new HashMap<>();
    private final List<Kategorie> kategorienZumSpeichern = new ArrayList<>();
    private final Map<Kategorie, List<String>> produktZuordnungen = new HashMap<>();

    private int kategorieZahl = 0;
    private int zugeordneteProdukte = 0;

    /**
     * Importiert alle Kategorien inkl. Produktverknüpfungen mit vollständigem Pfad.
     * 
     * @param rootCategories Liste der Wurzelkategorien
     */
    @Transactional
    public void importCategories(List<CategoryData> rootCategories) {
        for (CategoryData root : rootCategories) {
            saveCategoryRecursive(root, null);
        }

        kategorieRepository.saveAll(kategorienZumSpeichern);

        Set<String> bekannteProduktIds = produktRepository.findAllProduktIds();
        Map<String, Produkt> produktMap = new HashMap<>();
        for (String produktId : bekannteProduktIds) {
            produktMap.put(produktId, produktRepository.getReferenceById(produktId));
        }

        for (Map.Entry<Kategorie, List<String>> entry : produktZuordnungen.entrySet()) {
            Kategorie kategorie = entry.getKey();
            for (String asin : entry.getValue()) {
                Produkt produkt = produktMap.get(asin);
                if (produkt != null) {
                    produkt.getKategorien().add(kategorie);
                    zugeordneteProdukte++;
                } else {
                    ImportStatistik.increment("[KategorieImport] product with ASIN not found");
                    String msg = "Product with ASIN " + asin + " not found. [Ignored]";
                    log.warn(msg);
                    ImportLogger.logWarning("KategorieImport", asin, msg);
                }
            }
        }

        System.out.println("Anzahl an Kategorien: " + kategorieZahl);
        System.out.println("Anzahl an zugeordneten Produkten: " + zugeordneteProdukte);
    }

    /**
     * Speichert rekursiv Kategorien mit Pfad.
     * 
     * @param data Kategorie-Eintrag
     * @param pfad bisheriger Pfad
     */
    private void saveCategoryRecursive(CategoryData data, String pfad) {
        String name = data.getName();
        if (name == null || name.isBlank()) return;

        String trimmed = name.trim();
        String currentPfad = (pfad == null) ? trimmed : pfad + " > " + trimmed;

        Kategorie kategorie = kategorienCache.get(currentPfad);
        if (kategorie == null) {
            kategorie = new Kategorie();
            kategorie.setName(trimmed);
            kategorie.setPfad(currentPfad);
            kategorienCache.put(currentPfad, kategorie);
            kategorienZumSpeichern.add(kategorie);
            kategorieZahl++;
        }

        for (CategoryData sub : data.getSubcategories()) {
            saveCategoryRecursive(sub, currentPfad);
        }

        // Nur bei Blattknoten Produkte zuordnen
        if (data.getSubcategories().isEmpty() && !data.getItems().isEmpty()) {
            produktZuordnungen.computeIfAbsent(kategorie, k -> new ArrayList<>())
                    .addAll(data.getItems());
        }
    }
}
