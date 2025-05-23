package com.backend.service.dto;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

// Klasse für MusikCD-spezifische Daten
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class MusicSpecData {

    @XmlElement(name = "binding")
    private String binding;

    @XmlElement(name = "format")
    private FormatData format;

    @XmlElement(name = "num_discs")
    private Integer numDiscs;

    @XmlElement(name = "releasedate")
    private String releaseDate;

    @XmlElement(name = "upc")
    private UPCData upc;

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class UPCData {

        @XmlValue
        private String value;

        @XmlAttribute(name = "value")
        private String alternativeValue;

        /*
         * Aufgrund verschiedener Darstellungsformenn des Wertes in unterschiedlichen
         * XML-Datein werden beide Varianten betrachtet und bei Nichtverfügbarkeit des
         * Einen das jeweils Andere zurückgegeben.
         */
        public String getValue() {
            return value == null || value.isBlank() ? alternativeValue : value;
        }

    }

}
