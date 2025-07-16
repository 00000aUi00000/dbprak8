package com.frontend.dto;

import java.time.LocalDate;

public class RezensionDTO {
    public Long rezensionId;
    public Long kundeId;
    public String produktId;
    public Integer punkte;
    public String zusammenfassung;
    public String text;
    public String username;
    public LocalDate datum;
    public Integer anzahlNuetzlich;

    public RezensionDTO() {}

    public RezensionDTO(
        Long rezensionId,
        Long kundeId,
        String produktId,
        Integer punkte,
        String zusammenfassung,
        String text,
        String username,
        LocalDate datum,
        Integer anzahlNuetzlich
    ) {
        this.rezensionId = rezensionId;
        this.kundeId = kundeId;
        this.produktId = produktId;
        this.punkte = punkte;
        this.zusammenfassung = zusammenfassung;
        this.text = text;
        this.username = username;
        this.datum = datum;
        this.anzahlNuetzlich = anzahlNuetzlich;
    }
}
