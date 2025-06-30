package com.frontend.dto;

import com.backend.entity.Produkt;

public class TopProduktDTO {

    public String produktId;
    public String titel;
    public String typ;
    public Double rating;
    public Long anzahlRezensionen;

    public TopProduktDTO(Produkt produkt, Long anzahlRezensionen) {
        this.produktId = produkt.getProduktId();
        this.titel = produkt.getTitel();
        this.typ = produkt.getClass().getSimpleName();
        this.rating = produkt.getRating();
        this.anzahlRezensionen = anzahlRezensionen;
    }

}
