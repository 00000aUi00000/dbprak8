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
import com.backend.repository.ProduktkategorieRepository;
import com.backend.entity.Produktkategorie;

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
    private final ProduktkategorieRepository produktkategorieRepository;
    private final List<Produktkategorie> produktkategorieZumSpeichern = new ArrayList<>();

    private final Map<String, Kategorie> kategorienCache = new HashMap<>();
    private final List<Kategorie> kategorienZumSpeichern = new ArrayList<>();
    private final List<KategorieHierarchie> hierarchieZumSpeichern = new ArrayList<>();
    private final Map<Kategorie, List<String>> produktZuordnungen = new HashMap<>();

    private Integer kategorieZahl = 0;
    private Integer hierarchieZahl = 0;
    private Integer zugeordneteProdukte = 0;

    /**
     * Importiert alle Kategorien und Kategoriehierarchien basierend auf den
     * gegebenen Hauptkategorien.
     * 
     * Loggt auftretende Fehler in Konsole und Datei.
     * 
     * @param rootCategories alle Hauptkategorien
     */
    @Transactional // Ausfügen aller DB-Operationen innerhalb der Methode in einer Transaktion
    public void importCategories(List<CategoryData> rootCategories) {
        for (CategoryData root : rootCategories) {
            saveCategoryRecursive(root, null, null);
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
                    Produktkategorie pk = new Produktkategorie();
                    pk.setProdukt(produkt);
                    pk.setKategorie(kategorie);
                    pk.setHauptkategorie(findeHauptkategorie(kategorie)); // nur ID wird gespeichert
                    produktkategorieZumSpeichern.add(pk);
                    zugeordneteProdukte++;
                } else {
                    ImportStatistik.increment("[KategorieImport] product with ASIN not found");
                    String msg = "Product with ASIN " + asin + " not found. [Ignored]";
                    log.warn(msg);
                    ImportLogger.logWarning("KategorieImport", asin, msg);
                }
            }
        }
        produktkategorieRepository.saveAll(produktkategorieZumSpeichern);

        System.out.println("Anzahl an Kategorien: " + kategorieZahl);
        System.out.println("Anzahl an Kategorienhierarchien: " + hierarchieZahl);
        System.out.println("Anzahl an zugeordneten Produkten: " + zugeordneteProdukte);
    }

    /**
     * Speichert rekursiv die Kategorie basierend auf der {@link CategoryData}
     * sowie dessen Unterkategorien und stellt die Beziehung zur Parent-Kategorie auf.
     * 
     * @param data die zugrundeliegenden Kategorie-Daten
     * @param parent die zugehörige Parent-Kategorie
     */
    private void saveCategoryRecursive(CategoryData data, Kategorie parent, Kategorie hauptkategorie) {
        String name = data.getName();
        if (name == null || name.isBlank())
            return;

        String nameTrimmed = name.trim();
        Kategorie kategorie = kategorienCache.get(nameTrimmed);

        if (kategorie == null) {
            kategorie = new Kategorie();
            kategorie.setName(nameTrimmed);
            kategorienCache.put(nameTrimmed, kategorie);
            kategorienZumSpeichern.add(kategorie);
            kategorieZahl++;
        }

        if (parent == null) {
            hauptkategorie = kategorie;
        }

        if (parent != null) {
            KategorieHierarchie relation = new KategorieHierarchie();
            relation.setParent(parent);
            relation.setChild(kategorie);
            hierarchieZumSpeichern.add(relation);
            hierarchieZahl++;
        }

        // Produkte, die direkt an dieser Kategorie hängen, zählen auch genau hierhin
        if (!data.getItems().isEmpty()) {
            for (String asin : data.getItems()) {
                // Produktzuordnung: Kategorie + dazugehörige Hauptkategorie
                produktZuordnungen.computeIfAbsent(kategorie, k -> new ArrayList<>()).add(asin);
                // Du speicherst später die Hauptkategorie mit: findeHauptkategorie(...) nicht nötig
                // Alternativ: Map<Kategorie, Map<ASIN, Hauptkategorie>> wäre auch möglich
            }
        }

        // Unterkategorien verarbeiten (Hauptkategorie bleibt dieselbe)
        for (CategoryData sub : data.getSubcategories()) {
            saveCategoryRecursive(sub, kategorie, hauptkategorie);
        }
    }

    private Kategorie findeHauptkategorie(Kategorie start) {
        Kategorie current = start;
        Set<Kategorie> visited = new HashSet<>();

        while (true) {
            boolean foundParent = false;

            for (KategorieHierarchie h : hierarchieZumSpeichern) {
                if (h.getChild().equals(current)) {
                    current = h.getParent();

                    if (!visited.add(current)) {
                        throw new IllegalStateException("Zyklische Kategoriebeziehung erkannt");
                    }

                    foundParent = true;
                    break;
                }
            }

            if (!foundParent) {
                return current;
            }
        }
    }
}
