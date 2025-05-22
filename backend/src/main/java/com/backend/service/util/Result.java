package com.backend.service.util;

import lombok.EqualsAndHashCode;

/**
 * Klasse zum Darstellen von Ergebnissen mit möglichen Fehlern.
 * Dient als Rückgabewert und Validierung erfolgreicher Methodenaufrufe
 * bzw. einfache Fehlerweiterleitung.
 * 
 * Dabei kann das Ergebnis leer ({@link Type#EMPTY}), ein Wert ({@link Type#VALID}), 
 * eine Fehlernachricht ({@link Type#ERROR}) oder ein Wert mit einer Fehlernachricht
 * ({@link Type#WARNING}) vorhanden sein.
 * 
 * @param <T> Typ des Ergebniswertes
 */
@EqualsAndHashCode // Lombok-Annotation für #equals und #hashCode Methoden
public class Result<T> {

    private final T value;
    private final String errorMessage;
    private final Type type;

    // leeres Result
    public static <T> Result<T> empty() {
        return new Result<>(null, null, Type.EMPTY);
    }

    // Result mit konkretem Wert
    public static <T> Result<T> of(T value) {
        if (value == null)
            throw new IllegalArgumentException("Value may not be null.");
        return new Result<>(value, null, Type.VALID);
    }

    // Result mit keinem Wert und Fehlermeldung
    public static <T> Result<T> error(String message) {
        return new Result<>(null, message, Type.ERROR);
    }

    // Result mit Warnung – z. B. gültiger Wert mit Einschränkungen
    public static <T> Result<T> warning(T value, String message) {
        if (value == null)
            throw new IllegalArgumentException("Value may not be null.");
        return new Result<>(value, message, Type.WARNING);
    }

    // private Konstruktor, damit Instanziierung nur über Static-Factory-Methods
    private Result(T value, String errorMessage, Type type) {
        this.value = value;
        this.errorMessage = errorMessage;
        this.type = type;
    }

    // Getter für Wert und Fehlernachricht

    public T getValue() {
        return value;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    // Methoden zur Abfrage des Typs

    public boolean isValid() {
        return type == Type.VALID;
    }

    public boolean isError() {
        return type == Type.ERROR;
    }

    public boolean isEmpty() {
        return type == Type.EMPTY;
    }
    
    public boolean isWarning() {
        return type == Type.WARNING;
    }

    /**
     * Typ eines Results
     */
    public static enum Type {

        /**
         * Zeigt an, dass das Ergebnis einen gültigen Wert ungleich null hat.
         */
        VALID,
        /**
         * Zeigt an, dass ein Fehler mit einer Fehlernachricht vorhanden ist.
         */
        ERROR,
        /**
         * Zeigt an, dass das Ergebnis leer ist.
         */
        EMPTY,
        /**
         * Zeigt an, dass ein Wert und eine Fehlernachricht vorhanden sind.
         */
        WARNING;

    }

}
