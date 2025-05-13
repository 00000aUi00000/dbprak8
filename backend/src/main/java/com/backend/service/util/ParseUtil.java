package com.backend.service.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.backend.service.dto.FormatData;

public final class ParseUtil {

    private ParseUtil() {
        throw new UnsupportedOperationException("Instantiation of this class is prohibited.");
    }

    public static Double parseDouble(String text) {
        if (text == null || text.isBlank())
            return null;
        try {
            return Double.parseDouble(text);
        } catch (final NumberFormatException ignored) {
            return null;
        }
    }

    public static Integer parseInteger(String text) {
        if (text == null || text.isBlank())
            return null;
        try {
            return Integer.parseInt(text);
        } catch (final NumberFormatException ignored) {
            return null;
        }
    }

    public static LocalDate parseDate(String text) {
        if (text == null || text.isBlank())
            return null;
        try {
            return LocalDate.parse(text);
        } catch (final DateTimeParseException ignored) {
            return null;
        }
    }

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