package com.backend.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.service.dto.CategoriesData;
import com.backend.service.dto.ItemData;
import com.backend.service.dto.ShopData;
import com.backend.service.parser.CategoriesDatabaseParser;
import com.backend.service.parser.CategoriesFileParser;
import com.backend.service.parser.RezensionImportService;
import com.backend.service.parser.SimilarProductParser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MainImportService {
    private final SimilarProductParser similarProductParser;
    private final ShopDatabaseParser shopDatabaseParser;
    private final CategoriesDatabaseParser categoriesDatabaseParser;
    private final RezensionImportService rezensionImportService;

    private final List<ItemData> items = new LinkedList<>();

    // Zentraler Aufruf der Importfunktionen
    public void importAll() {
        importShop("files/leipzig_transformed.xml");
        importShop("files/dresden.xml");

        similarProductParser.parseSimilarProducts(this.items);

        importCategories("files/categories.xml");
        importReviews("files/reviews.csv");

        System.out.println("Import abgeschlossen, Gut gemacht!");
        printImportLog();
    }

    // Importfunktion Shop + Artikel
    private void importShop(String resourcePath) {
        ShopImportParser parser = new ShopImportParser();
        ShopData shopData = parseXmlFile(resourcePath, parser::parseShopFile);

        if (shopData != null) {
            System.out.println("Shop wird geladen: " + shopData.getName());
            shopDatabaseParser.parseData(shopData);
            items.addAll(shopData.getItems());
            System.out.println("Shop fertig geladen: " + shopData.getName());
        } else {
            System.out.println("Shop konnte nicht geladen werden: " + resourcePath);
        }
    }

    // Importfunktion Kategorien
    private void importCategories(String resourcePath) {
        CategoriesFileParser parser = new CategoriesFileParser();
        CategoriesData data = parseXmlFile(resourcePath, parser::parseCategoriesFile);

        if (data != null) {
            System.out.println("\nKategorien werden importiert...");
            categoriesDatabaseParser.importCategories(data.getCategories());
            System.out.println("Kategorien wurden erfolgreich importiert.");
        } else {
            System.out.println("Kategorien konnten nicht geladen werden: " + resourcePath);
        }
    }

    // Importfunktion Reviews
    private void importReviews(String csvPath) {
        System.out.println("Importiere Rezensionen...");
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(csvPath)) {
            if (inputStream == null) {
                System.err.println("Rezensionen-Datei nicht gefunden im Classpath: " + csvPath);
                return;
            }

            // Temporäre Datei erstellen, da OpenCSV ein File erwartet
            File tempFile = File.createTempFile("rezensionen", ".csv");
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            rezensionImportService.importRezensionen(tempFile.getAbsolutePath());

            System.out.println("Rezensionen wurden erfolgreich importiert.");
            tempFile.deleteOnExit();
        } catch (IOException e) {
            System.err.println("Fehler beim Import der Rezensionen aus: " + csvPath);
            e.printStackTrace();
        }
    }

    // Hilfsfunktion für Parsen
    private <T> T parseXmlFile(String resourcePath, FileParser<T> parser) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                System.err.println("Datei nicht gefunden im Classpath: " + resourcePath);
                return null;
            }

            File tempFile = File.createTempFile("import", ".xml");
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            tempFile.deleteOnExit();

            return parser.parse(tempFile);

        } catch (IOException e) {
            System.err.println("Fehler beim Laden der Datei aus resources: " + resourcePath);
            e.printStackTrace();
            return null;
        }
    }

    // Interface für verschiedene Parser
    @FunctionalInterface
    private interface FileParser<T> {
        T parse(File file);
    }

    private void printImportLog() {
        File logFile = new File("import-log.txt");
        if (logFile.exists()) {
            System.out.println("\n--- Inhalt von import-log.txt ---");
            try {
                Files.lines(logFile.toPath()).forEach(System.out::println);
            } catch (IOException e) {
                System.err.println("Fehler beim Lesen der Logdatei: " + e.getMessage());
            }
            System.out.println("--- Ende Log ---\n");
        } else {
            System.out.println("Keine Logdatei gefunden (import-log.txt fehlt).");
        }
    }
    
}
