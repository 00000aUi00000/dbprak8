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
public class AuthorData {

    @XmlValue
    private String name;

    @XmlAttribute(name = "name")
    private String alternativeName;

    public String getName() {
        return name == null || name.isBlank() ? alternativeName : name;
    }

}
