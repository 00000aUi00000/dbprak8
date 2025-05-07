package com.backend.service;

import com.backend.service.dto.ShopData;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Service
public class MainImportService {

    public void importAll() {
        importShop("files/leipzig_transformed.xml");
        importShop("files/dresden.xml");
        importCategories("files/categories.xml");
        importReviews("files/rezensionen.csv");
    }

    private void importShop(String resourcePath) {
        ShopImportParser parser = new ShopImportParser();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                System.err.println("Datei nicht gefunden im Classpath: " + resourcePath);
                return;
            }

            // Temporäre Datei erstellen, weil JAXB File erwartet
            File tempFile = File.createTempFile("import", ".xml");
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            ShopData shopData = parser.parseShopFile(tempFile);

            if (shopData != null) {
                System.out.println("Importierter Shop: " + shopData.getName());
                // Weiterverarbeitung folgt später
            } else {
                System.out.println("Shop konnte nicht geladen werden: " + resourcePath);
            }

            tempFile.deleteOnExit();

        } catch (IOException e) {
            System.err.println("Fehler beim Laden der Datei aus resources: " + resourcePath);
            e.printStackTrace();
        }
    }

    //TBD
    private void importCategories(String xmlPath) {
    }

    //TBD
    private void importReviews(String csvPath) {
    }
}
