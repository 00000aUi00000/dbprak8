package com.backend.service.parser;

import com.backend.entity.Kategorie;
import com.backend.entity.KategorieHierarchie;
import com.backend.entity.Produkt;
import com.backend.repository.KategorieHierarchieRepository;
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
    private final KategorieHierarchieRepository kategorieHierarchieRepository;
    private final ProduktRepository produktRepository;

    private final Map<String, Kategorie> kategorienCache = new HashMap<>();
    private final List<Kategorie> kategorienZumSpeichern = new ArrayList<>();
    private final List<KategorieHierarchie> hierarchieZumSpeichern = new ArrayList<>();
    private final Map<Kategorie, List<String>> produktZuordnungen = new HashMap<>();

    private Integer kategorieZahl = 0;
    private Integer hierarchieZahl = 0;
    private Integer zugeordneteProdukte = 0;

    @Transactional
    public void importCategories(List<CategoryData> rootCategories) {
        for (CategoryData root : rootCategories) {
            saveCategoryRecursive(root, null);
        }


       // Kategorien speichern -> IDs werden erzeugt
        kategorieRepository.saveAll(kategorienZumSpeichern);

        // Hierarchie aufbauen -> damit alles konsistent ist
        for (KategorieHierarchie h : hierarchieZumSpeichern) {
            if (h.getParent().getKategorieId() == null || h.getChild().getKategorieId() == null) {
                throw new IllegalStateException("Kategorie in Hierarchie noch nicht gespeichert.");
            }
        }

        // Hierarchie speichern
        kategorieHierarchieRepository.saveAll(hierarchieZumSpeichern);

                // Produkt-IDs laden und einmalig in Set legen
        Set<String> bekannteProduktIds = produktRepository.findAllProduktIds();

        // Produkt-Zuordnungen jetzt verarbeiten
        for (Map.Entry<Kategorie, List<String>> entry : produktZuordnungen.entrySet()) {
            Kategorie kategorie = entry.getKey();
            for (String asin : entry.getValue()) {
                if (bekannteProduktIds.contains(asin)) {
                    Produkt produkt = produktRepository.getReferenceById(asin);
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
       System.out.println("Anzahl an Kategorienhierarchien: " + hierarchieZahl);
       System.out.println("Anzahl an zugeordneten Produkten: " + zugeordneteProdukte);   
    }

    private void saveCategoryRecursive(CategoryData data, Kategorie parent) {
        String name = data.getName();
        if (name == null || name.isBlank()) return;

        String nameTrimmed = name.trim();
        Kategorie kategorie = kategorienCache.get(nameTrimmed); 

        if (kategorie == null) {
            kategorie = new Kategorie();
            kategorie.setName(nameTrimmed);
            kategorienCache.put(nameTrimmed, kategorie);
            kategorienZumSpeichern.add(kategorie);
            kategorieZahl++;
        }

        if (parent != null) {
            KategorieHierarchie relation = new KategorieHierarchie();
            relation.setParent(parent);
            relation.setChild(kategorie);
            hierarchieZumSpeichern.add(relation);
            hierarchieZahl++;
        }

        // Rekursiv Unterkategorien verarbeiten
        for (CategoryData sub : data.getSubcategories()) {
            saveCategoryRecursive(sub, kategorie);
        }

        // Produkt-Zuordnungen vormerken
        if (!data.getItems().isEmpty()) {
            produktZuordnungen.computeIfAbsent(kategorie, k -> new ArrayList<>())
                .addAll(data.getItems());
        }
    }
    
}
