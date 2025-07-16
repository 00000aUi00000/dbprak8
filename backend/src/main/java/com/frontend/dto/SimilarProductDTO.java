package com.frontend.dto;

public class SimilarProductDTO {

    private String produktId;
    private String titel;
    private Class<?> typ;
    private Double billigsterPreis;

    public SimilarProductDTO(String produktId, String titel, Class<?> typ, Double billigsterPreis) {
        this.produktId = produktId;
        this.titel = titel;
        this.typ = typ;
        this.billigsterPreis = billigsterPreis;
    }

    public String getProduktId() {
        return produktId;
    }

    public String getTitel() {
        return titel;
    }

    public String getTyp() {
        return typ != null ? typ.getSimpleName() : "-";
    }

    public Double getBilligsterPreis() {
        return billigsterPreis;
    }
}
