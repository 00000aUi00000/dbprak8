package com.backend.service.util;

import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class ImportLogger {

    private static final String LOG_FILE_PATH = "import-log.txt";

    public static void logError(String context, Object data, String message) {
        logToFile("ERROR", context, data, message);
    }

    public static void logWarning(String context, Object data, String message) {
        logToFile("WARNING", context, data, message);
    }

    private static void logToFile(String level, String context, Object data, String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE_PATH, true))) {
            //writer.print("[" + LocalDateTime.now() + "] [" + level + "] [" + context + "] ");
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
