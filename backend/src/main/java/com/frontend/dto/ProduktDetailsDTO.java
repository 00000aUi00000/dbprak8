package com.frontend.dto;

import java.time.LocalDate;

import com.backend.entity.Produkt;

public class ProduktDetailsDTO {

    public String produktId;
    public String typ;
    public String titel;
    public Double rating;
    public Integer verkaufsrang;
    public String bild;
    public LocalDate erscheinungsdatum;

    public ProduktDetailsDTO(Produkt produkt, String typ) {
        this.typ = typ;
        this.produktId = produkt.getProduktId();
        this.titel = produkt.getTitel();
        this.rating = produkt.getRating();
        this.verkaufsrang = produkt.getVerkaufsrang();
        this.bild = produkt.getBild();
        this.erscheinungsdatum = produkt.getErscheinungsdatum();
    }

}
