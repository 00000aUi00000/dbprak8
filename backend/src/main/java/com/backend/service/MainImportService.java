package com.backend.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.service.dto.ShopData;

@Service
public class MainImportService {

    @Autowired
    private ShopDatabaseParser shopDatabaseParser;

    public void importAll() {
        importShop("files/leipzig_transformed.xml");
        importShop("files/dresden.xml");
        importCategories("files/categories.xml");
        importReviews("files/rezensionen.csv");
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
                shopDatabaseParser.parseData(shopData);
            } else {
                System.out.println("Shop konnte nicht geladen werden: " + resourcePath);
            }

            tempFile.deleteOnExit();

        } catch (IOException e) {
            System.err.println("Fehler beim Laden der Datei aus resources: " + resourcePath);
            e.printStackTrace();
        }
    }

    // TBD
    private void importCategories(String xmlPath) {
    }

    // TBD
    private void importReviews(String csvPath) {
    }

}
