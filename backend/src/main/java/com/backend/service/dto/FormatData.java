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
public class FormatData {

    @XmlValue
    private String value;

    @XmlAttribute(name = "value")
    private String alternativeValue;

    public String getValue() {
        return value == null || value.isBlank() ? alternativeValue : value;
    }

}