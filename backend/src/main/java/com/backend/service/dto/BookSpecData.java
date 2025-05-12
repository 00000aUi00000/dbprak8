package com.backend.service.dto;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class BookSpecData {

    @XmlElement(name = "publisher")
    private String publisher;

    @XmlElement(name = "releasedate")
    private String releasedate;

    @XmlElement(name = "isbn")
    private ISBNData isbnData;

    @XmlElement(name = "pages")
    private Integer pages;
}
