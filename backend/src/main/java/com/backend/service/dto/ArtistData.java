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

    public String getName() {
        return name == null || name.isBlank() ? alternativeName : name;
    }

}
