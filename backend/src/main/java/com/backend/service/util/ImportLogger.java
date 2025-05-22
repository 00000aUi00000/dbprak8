package com.backend.service.util;

import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

// Klasse zum Logging von Fehlern und Warnmeldungen in eine Datei
@Slf4j
public class ImportLogger {

    /**
     * Dateipfad der Log-Datei
     */
    private static final String LOG_FILE_PATH = "import-log.txt";

    /**
     * Loggt eine Nachricht mit dem gegebenen Context und dem nachricht-bezogenen
     * Objekt mit Level "ERROR" in eine {@link #LOG_FILE_PATH Datei}.
     * 
     * @param context der Kontext in dem die Nachricht auftritt
     * @param data    das zur Nachricht gehörende Objekt, um das es sich handelt
     * @param message die grundlegende Nachricht
     */
    public static void logError(String context, Object data, String message) {
        logToFile("ERROR", context, data, message);
    }

    /**
     * Loggt eine Nachricht mit dem gegebenen Context und dem nachricht-bezogenen
     * Objekt mit Level "WARNING" in eine {@link #LOG_FILE_PATH Datei}.
     * 
     * @param context der Kontext in dem die Nachricht auftritt
     * @param data    das zur Nachricht gehörende Objekt, um das es sich handelt
     * @param message die grundlegende Nachricht
     */
    public static void logWarning(String context, Object data, String message) {
        logToFile("WARNING", context, data, message);
    }

    /**
     * Loggt eine Nachricht mit dem gegebenen Context und dem nachricht-bezogenen
     * Objekt mit einem bestimmten Level in eine {@link #LOG_FILE_PATH Datei}.
     * 
     * @param level   der Wertungsgrad des Log-Eintrags
     * @param context der Kontext in dem die Nachricht auftritt
     * @param data    das zur Nachricht gehörende Objekt, um das es sich handelt
     * @param message die grundlegende Nachricht
     */
    private static void logToFile(String level, String context, Object data, String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE_PATH, true))) { // Anfügen des Eintrags in Datei
            // writer.print("[" + LocalDateTime.now() + "] [" + level + "] [" + context + "]
            // ");
            writer.print("[" + level + "] [" + context + "] ");
            writer.print("Message: " + message);
            if (data != null) {
                writer.println(" Data: " + data.toString());
            }
            // writer.println("---");
        } catch (IOException e) {
            log.error("Fehler beim Schreiben ins Import-Log: {}", e.getMessage());
        }
    }

}
