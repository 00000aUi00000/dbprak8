package com.backend.service.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public final class ParseUtil {

    private ParseUtil() {
        throw new UnsupportedOperationException("Instantiation of this class is prohibited.");
    }

    public static Integer parseInteger(String text) {
        if (text == null)
            return null;
        try {
            return Integer.parseInt(text);
        } catch (final NumberFormatException ignored) {
            return null;
        }
    }

    public static LocalDate parseDate(String text) {
        if (text == null)
            return null;
        try {
            return LocalDate.parse(text);
        } catch (final DateTimeParseException ignored) {
            return null;
        }
    }

}