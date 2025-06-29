package com.frontend.dto;

import com.backend.entity.Angebotsdetails;

public class AngebotDetailsDTO {
    
    public Long angebotId;
    public String zustand;
    public Double preis;

    public AngebotDetailsDTO(Angebotsdetails details) {
        this.angebotId = details.getAngebot().getAngebotId();
        this.zustand = details.getZustand();
        this.preis = details.getPreis();
    }

}
