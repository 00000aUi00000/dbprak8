package com.backend.service.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class PriceData {

    @XmlAttribute(name = "mult")
    private String mult;

    @XmlAttribute(name = "state")
    private String state;

    @XmlAttribute(name = "currency")
    private String currency;

    @XmlValue
    private Double value;

}
