package com.backend.service.util;

public class Result<T> {

    private final T value;
    private final String errorMessage;
    private final boolean present;

    // Result, das anzeigt, dass es keinen Wert per se gibt, jedoch so behandelt wird, als ob es diesen gibt
    public static <T> Result<T> empty() {
        return new Result<>(null, null, true);
    }

    // Result mit konkretem Wert
    public static <T> Result<T> of(T value) {
        return new Result<>(value, null, true);
    }

    // Result mit keinem Wert und Fehlermeldung
    public static <T> Result<T> error(String message) {
        return new Result<>(null, message, false);
    }

    private Result(T value, String errorMessage, boolean present) {
        this.value = value;
        this.errorMessage = errorMessage;
        this.present = present;
    }

    public T getValue() {
        return value;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isPresent() {
        return present;
    }

    public boolean hasValue() {
        return getValue() != null;
    }

}
