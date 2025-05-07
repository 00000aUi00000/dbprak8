package com.backend.service.dto;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class MusicSpecData {

    @XmlElement(name = "binding")
    private String binding;

    @XmlElement(name = "format")
    private String format;

    @XmlElement(name = "num_discs")
    private Integer numDiscs;

    @XmlElement(name = "releasedate")
    private String releaseDate;

    @XmlElement(name = "upc")
    private String upc;

}
