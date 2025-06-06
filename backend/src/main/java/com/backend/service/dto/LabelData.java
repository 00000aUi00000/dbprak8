package com.backend.service.dto;

import jakarta.xml.bind.annotation.XmlValue;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlAccessType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class LabelData {

    @XmlValue
    private String name;

    @XmlAttribute(name = "name")
    private String alternativeName;

    /*
     * Aufgrund verschiedener Darstellungsformenn des Labels in unterschiedlichen
     * XML-Datein werden beide Varianten betrachtet und bei Nichtverfügbarkeit des
     * Einen das jeweils Andere zurückgegeben.
     */
    public String getName() {
        return name == null || name.isBlank() ? alternativeName : name;
    }

}
