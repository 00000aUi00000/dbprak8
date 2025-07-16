package com.frontend.dto;

import java.time.LocalDate;

public class ProduktDto {
    public String produktId;
    public String titel;
    public String typ;
    public Double rating;
    public Integer verkaufsrang;
    public LocalDate erscheinungsdatum;


    public ProduktDto(String produktId, String titel, String typ) {
        this.produktId = produktId;
        this.titel = titel;
        this.typ = typ;
    }

    public ProduktDto(String produktId, String titel, String typ,
                      Double rating, Integer verkaufsrang, LocalDate erscheinungsdatum) {
        this.produktId = produktId;
        this.titel = titel;
        this.typ = typ;
        this.rating = rating;
        this.verkaufsrang = verkaufsrang;
        this.erscheinungsdatum = erscheinungsdatum;
    }
}
