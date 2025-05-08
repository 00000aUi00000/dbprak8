package com.backend.service.dto;

import jakarta.xml.bind.annotation.XmlValue;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAccessType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class ActorData {

    @XmlValue
    private String name;
}
