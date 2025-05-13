package com.backend.service.parser;

import com.backend.entity.Kategorie;
import com.backend.entity.KategorieHierarchie;
import com.backend.repository.KategorieHierarchieRepository;
import com.backend.repository.KategorieRepository;
import com.backend.repository.ProduktRepository;
import com.backend.service.dto.CategoryData;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

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

    private Integer kategorieZahl = 0;
    private Integer hierarchieZahl = 0;

    @Transactional
    public void importCategories(List<CategoryData> rootCategories) {
        for (CategoryData root : rootCategories) {
            saveCategoryRecursive(root, null);
        }

       System.out.println("Anzahl an Kategorien: " + kategorieZahl);
       System.out.println("Anzahl an Kategorienhierarchien: " + hierarchieZahl);

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

        // TODO: Produkt-Zuordnung folgt später
    }
}
