package com.frontend.dto;

public class ProduktDto {
    public String produktId;
    public String titel;
    public String typ;

    public ProduktDto(String produktId, String titel, String typ) {
        this.produktId = produktId;
        this.titel = titel;
        this.typ = typ;
    }
}

