package com.backend.service.dto;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

// Klasse für Buch-spezifische Daten
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class BookSpecData {

    @XmlElement(name = "publication")
    private Publication publication;

    @XmlElement(name = "isbn")
    private ISBNData isbnData;

    @XmlElement(name = "pages")
    private Integer pages;

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Publication {

        @XmlAttribute(name = "date")
        private String value;

    }

}
