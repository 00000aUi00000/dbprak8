package com.backend.service.dto;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

// Klasse für DVD-spezifische Daten
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class DVDSpecData {

    @XmlElement(name = "format")
    private FormatData format;

    @XmlElement(name = "releasedate")
    private String releasedate;

    @XmlElement(name = "regioncode")
    private Integer regionCode;

    @XmlElement(name = "runningtime")
    private Integer runningTime;

}
