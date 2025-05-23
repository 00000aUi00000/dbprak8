package com.backend.service.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class ArtistData {

    @XmlValue
    private String name;

    @XmlAttribute(name = "name")
    private String alternativeName;

    /*
     * Aufgrund verschiedener Darstellungsformenn des Artist-Namens in unterschiedlichen
     * XML-Datein werden beide Varianten betrachtet und bei Nichtverfügbarkeit des
     * Einen das jeweils Andere zurückgegeben.
     */
    public String getName() {
        return name == null || name.isBlank() ? alternativeName : name;
    }

}
