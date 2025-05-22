package com.backend.service.parser;

import com.backend.entity.Produkt;
import com.backend.entity.Rezension;
import com.backend.repository.ProduktRepository;
import com.backend.repository.RezensionRepository;
import com.backend.service.util.ImportLogger;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

// Klasse zum Importieren von Rezensionen
@Slf4j
@Service
@RequiredArgsConstructor
public class RezensionImportService {

    private final RezensionRepository rezensionRepository;
    private final ProduktRepository produktRepository;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Importiert die Rezensionen in der CSV-Datei im gegebnen Pfad.
     * 
     * Loggt evtl. Fehler beim Parsen in Konsole und Datei.
     * 
     * @param csvPath der Dateipfad der CSV-Daten
     */
    public void importRezensionen(String csvPath) {
        try (CSVReader reader = new CSVReader(new FileReader(csvPath))) {
            String[] header = reader.readNext(); // Header-Zeile überspringen
            String[] line;

            // solange Zeilen vorhanden
            while ((line = reader.readNext()) != null) {
                if (line.length < 7) {
                    String msg = "Ungültige Zeile in CSV: " + line;
                    ImportLogger.logWarning("Rezension", line, msg); // Logging in File
                    log.warn(msg);
                    continue;
                }

                // Parsen der Daten in Zeile
                String productId = clean(line[0]);
                Integer rating = parseInteger(clean(line[1]));
                String helpfulStr = clean(line[2]);
                String dateStr = clean(line[3]);
                String username = clean(line[4]);
                String summary = clean(line[5]);
                String content = clean(line[6]);

                Integer helpful = parseInteger(helpfulStr);

                // Laden des gegebenden Produkts aus Datenbank
                Optional<Produkt> produktOpt = produktRepository.findById(productId);

                // Warnung, wenn Produkt nicht in Datenbank
                if (produktOpt.isEmpty()) {
                    String msg = "ProduktID nicht gefunden. [Ignored] " + productId;
                    ImportLogger.logError("Rezension", productId, msg);
                    log.error(msg);
                    continue;
                }

                // Warnung, wenn Rating nicht in [1,5]
                if (rating < 1 || rating > 5) {
                    String msg = "Rating not between 1 and 5. [Ignored] " + productId;
                    ImportLogger.logError("Rezension", productId + " " + username, msg);
                    log.error(msg);
                    continue;
                }

                // wenn vorhanden, aber negativ: Fehler
                if (helpful != null && helpful < 0) {
                    String msg = "Helpful count is negative. [Ignored] " + productId;
                    ImportLogger.logError("Rezension", productId + " " + username, msg);
                    log.error(msg);
                    continue;
                }

                Produkt produkt = produktOpt.get();

                // Rezension anlegen
                Rezension rezension = new Rezension();
                rezension.setProdukt(produkt);
                rezension.setUsername(username);
                rezension.setPunkte(rating);
                rezension.setAnzahlNuetzlich(helpful);
                rezension.setDatum(parseDate(dateStr));
                rezension.setZusammenfassung(summary);
                rezension.setText(content);

                rezensionRepository.save(rezension);
            }
        } catch (IOException | CsvValidationException e) {
            log.error("Fehler beim Einlesen der Rezensionen: {}", e.getMessage(), e);
        }
    }

    private String clean(String input) {
        // Entfernen von überschüssigen Leerzeichen und "-Zeichen am Anfang / Ende
        return input != null ? input.trim().replaceAll("^\"|\"$", "") : "";
    }

    private Integer parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMAT);
        } catch (Exception e) {
            return null;
        }
    }

}
