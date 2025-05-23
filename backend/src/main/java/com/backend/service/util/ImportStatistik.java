package com.backend.service.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Klasse zum Zähler der Fehler bei der Verarbeitung der Importfiles
 * 
 */
public class ImportStatistik {
    private static final Map<String, Integer> counters = new HashMap<>();

    public static void increment(String key) {
        counters.merge(key, 1, Integer::sum);
    }

    public static void printSummary() {
        System.out.println("\n--- Importstatistik ---");
        counters.forEach((k, v) -> System.out.printf("%-40s: %d%n", k, v));
        System.out.println("\n--- Ende Importstatistik ---");
    }

    public static void reset() {
        counters.clear();
    }

    public static Map<String, Integer> getCounters() {
        return Map.copyOf(counters);
    }
}
