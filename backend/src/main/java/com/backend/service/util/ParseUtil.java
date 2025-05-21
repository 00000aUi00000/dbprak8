package com.backend.service.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.backend.service.dto.FormatData;

// Util-Klasse zum Parsen von String zu bestimmten Datentypen
public final class ParseUtil {

    // Util Klasse hat privaten Konstruktor und sollte auch nicht initialisiert werden
    private ParseUtil() {
        throw new UnsupportedOperationException("Instantiation of this class is prohibited.");
    }

    /**
     * Parst den gegebenen Wert zu einem Double und gibt null bei Misserfolg zurück.
     * 
     * @param text der zu parsende Text
     * @return Double Wert bei Erfolg, sonst null
     */
    public static Double parseDouble(String text) {
        if (text == null || text.isBlank())
            return null;
        try {
            return Double.parseDouble(text);
        } catch (final NumberFormatException ignored) {
            return null;
        }
    }

    /**
     * Parst den gegebenen Wert zu einem Integer und gibt null bei Misserfolg zurück.
     * 
     * @param text der zu parsende Text
     * @return Integer bei Erfolg, sonst null
     */
    public static Integer parseInteger(String text) {
        if (text == null || text.isBlank())
            return null;
        try {
            return Integer.parseInt(text);
        } catch (final NumberFormatException ignored) {
            return null;
        }
    }

    /**
     * Parst den gegebenen Wert zu einem Datum und gibt null bei Misserfolg zurück.
     * 
     * @param text der zu parsende Text
     * @return Datum bei Erfolg, sonst null
     */
    public static LocalDate parseDate(String text) {
        if (text == null || text.isBlank())
            return null;
        try {
            return LocalDate.parse(text);
        } catch (final DateTimeParseException ignored) {
            return null;
        }
    }

    /**
     * Konvertiert die FormatData null-safe zu einem String.
     * 
     * @param  format die zu parsende FormatData
     * @return String bei Erfolg, sonst null
     */
    public static String parseFormat(FormatData format) {
        if (format == null)
            return null;
        try {
            return format.toString();
        } catch (final Exception ignored) {
            return null;
        }
    }

}