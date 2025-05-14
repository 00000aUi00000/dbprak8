package com.backend.service.util;

public class Result<T> {

    private final T value;
    private final String errorMessage;
    private final Type type;

    // Result, das anzeigt, dass es keinen Wert per se gibt, jedoch so behandelt wird, als ob es diesen gibt
    public static <T> Result<T> empty() {
        return new Result<>(null, null, Type.EMPTY);
    }

    // Result mit konkretem Wert
    public static <T> Result<T> of(T value) {
        if (value == null) throw new IllegalArgumentException("Value may not be null.");
        return new Result<>(value, null, Type.VALID);
    }

    // Result mit keinem Wert und Fehlermeldung
    public static <T> Result<T> error(String message) {
        return new Result<>(null, message, Type.ERROR);
    }

    // Result mit Warnung – z. B. gültiger Wert mit Einschränkungen
    public static <T> Result<T> warning(T value, String message) {
        if (value == null) throw new IllegalArgumentException("Value may not be null.");
        return new Result<>(value, message, Type.WARNING);
    }

    private Result(T value, String errorMessage, Type type) {
        this.value = value;
        this.errorMessage = errorMessage;
        this.type = type;
    }

    public T getValue() {
        return value;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isValid() {
        return type == Type.VALID;
    }

    public boolean isError() {
        return type == Type.ERROR;
    }

    public boolean isEmpty() {
        return type == Type.EMPTY;
    }

    public static enum Type {

        VALID, 
        ERROR,
        EMPTY,
        WARNING;

    }

}
