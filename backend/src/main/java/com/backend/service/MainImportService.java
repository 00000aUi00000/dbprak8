package com.backend.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.service.dto.CategoriesData;
import com.backend.service.dto.ItemData;
import com.backend.service.dto.ShopData;
import com.backend.service.parser.CategoriesDatabaseParser;
import com.backend.service.parser.CategoriesFileParser;
import com.backend.service.parser.SimilarProductParser;

@Service
public class MainImportService {

    @Autowired
    private SimilarProductParser similarProductParser;
    @Autowired
    private ShopDatabaseParser shopDatabaseParser;
    @Autowired
    private CategoriesDatabaseParser categoriesDatabaseParser;

    private final List<ItemData> items;

    public MainImportService() {
        this.items = new LinkedList<>();
    }

    public void importAll() {
        importShop("files/leipzig_transformed.xml");
        importShop("files/dresden.xml");
    
        similarProductParser.parseSimilarProducts(this.items);

        importCategories("files/categories.xml");
        // importReviews("files/rezensionen.csv");

        System.out.println("Import abgeschlossen, Gut gemacht!");
    }

    private void importShop(String resourcePath) {
        ShopImportParser importParser = new ShopImportParser();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                System.err.println("Datei nicht gefunden im Classpath: " + resourcePath);
                return;
            }

            // Temporäre Datei erstellen, weil JAXB File erwartet
            File tempFile = File.createTempFile("import", ".xml");
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            ShopData shopData = importParser.parseShopFile(tempFile);

            if (shopData != null) {
                System.out.println("");
                System.out.println("Shop wird geladen: " + shopData.getName());
                shopDatabaseParser.parseData(shopData);
                items.addAll(shopData.getItems());
            } else {
                System.out.println("Shop konnte nicht geladen werden: " + resourcePath);
            }

            System.out.println("Shop fertig geladen: " + shopData.getName());
            tempFile.deleteOnExit();

        } catch (IOException e) {
            System.err.println("Fehler beim Laden der Datei aus resources: " + resourcePath);
            e.printStackTrace();
        }
    }

    // TBD
    private void importCategories(String resourcePath) {
        CategoriesFileParser importParser = new CategoriesFileParser();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                System.err.println("Datei nicht gefunden im Classpath: " + resourcePath);
                return;
            }

            // Temporäre Datei erstellen
            File tempFile = File.createTempFile("import-categories", ".xml");
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            CategoriesData categoriesData = importParser.parseCategoriesFile(tempFile);

            if (categoriesData != null) {
                System.out.println("\nKategorien werden importiert...");
                categoriesDatabaseParser.importCategories(categoriesData.getCategories());
                System.out.println("Kategorien wurden erfolgreich importiert.");
            } else {
                System.out.println("Kategorien konnten nicht geladen werden: " + resourcePath);
            }

            tempFile.deleteOnExit();

        } catch (IOException e) {
            System.err.println("Fehler beim Laden der Kategorien-Datei aus resources: " + resourcePath);
            e.printStackTrace();
        }
    }

    // TBD
    private void importReviews(String csvPath) {
        System.out.println("Importiere Reviews");
    }

}
