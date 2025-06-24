package com.frontend.dto;

import com.backend.entity.MusikCD;

public class MusikCDDetailsDTO extends ProduktDetailsDTO {

    public Integer anzahlcds;

    public MusikCDDetailsDTO(MusikCD musikCD) {
        super(musikCD, musikCD.getClass().getSimpleName());

        this.anzahlcds = musikCD.getAnzahlcds();
    }

}
