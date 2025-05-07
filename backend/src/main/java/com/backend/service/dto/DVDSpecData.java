package com.backend.service.dto;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class DVDSpecData {

    @XmlElement(name = "format")
    private FormatValue format;

    @XmlElement(name = "releasedate")
    private String releasedate;

    @XmlElement(name = "regioncode")
    private Integer regionCode;

    @XmlElement(name = "runningtime")
    private Integer runningTime;

    // Zum Formatieren von Format Informationen
    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class FormatValue {
        @XmlAttribute(name = "value")
        private String value;
    }
}
