package com.frontend.dto;

import com.backend.entity.Filiale;

public class FilialeDTO {
    
    public Long filialId;
    public String name;
    public String anschrift;

    public FilialeDTO(Filiale filiale) {
        this.filialId = filiale.getFilialId();
        this.name = filiale.getName();
        this.anschrift = filiale.getAnschrift();
    }

}
