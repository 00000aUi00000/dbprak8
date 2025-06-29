package com.frontend.dto;

import com.backend.entity.Angebotsdetails;
import com.backend.entity.Filiale;

public class AngebotDTO {

    public FilialeDTO filiale;
    public AngebotDetailsDTO details;

    public AngebotDTO(Filiale filiale, Angebotsdetails angebotsdetails) {
        this.filiale = new FilialeDTO(filiale);
        this.details = new AngebotDetailsDTO(angebotsdetails);
    }

}
