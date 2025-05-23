package com.backend.service.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Getter;
import lombok.Setter;

// Klasse für Verlagsdaten
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class PublisherData {

    @XmlValue
    private String name;

    @XmlAttribute(name = "name")
    private String alternativeName;

    /*
     * Aufgrund verschiedener Darstellungsformenn des Publisher-Namens in unterschiedlichen
     * XML-Datein werden beide Varianten betrachtet und bei Nichtverfügbarkeit des
     * Einen das jeweils Andere zurückgegeben.
     */
    public String getName() {
        return name == null || name.isBlank() ? alternativeName : name;
    }

}