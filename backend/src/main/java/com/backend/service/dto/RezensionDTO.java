package com.backend.service.dto;

import com.backend.entity.Rezension;
import java.time.LocalDate;

public record RezensionDTO(
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
    public static RezensionDTO fromEntity(Rezension rez) {
        return new RezensionDTO(
            rez.getRezensionId(),
            rez.getKunde() != null ? rez.getKunde().getKundeId() : null,
            rez.getProdukt().getProduktId(),
            rez.getPunkte(),
            rez.getZusammenfassung(),
            rez.getText(),
            rez.getUsername(),
            rez.getDatum(),
            rez.getAnzahlNuetzlich()
        );
    }
}
