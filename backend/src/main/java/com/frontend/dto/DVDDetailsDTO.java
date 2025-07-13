package com.frontend.dto;

import com.backend.entity.DVD;

public class DVDDetailsDTO extends ProduktDetailsDTO {

    public String format;
    public Integer laufzeit;
    public Integer regionCode;

    public DVDDetailsDTO(DVD dvd) {
        super(dvd, dvd.getClass().getSimpleName());

        this.format = dvd.getFormat();
        this.laufzeit = dvd.getLaufzeit();
        this.regionCode = dvd.getRegionCode();
    }

}
