package com.frontend.dto;

import com.backend.entity.Buch;

public class BuchDetailsDTO extends ProduktDetailsDTO {

    public String isbn;
    public Integer seitenanzahl;
    public String verlag;

    public BuchDetailsDTO(Buch buch) {
        super(buch, buch.getClass().getSimpleName());

        this.isbn = buch.getIsbn();
        this.seitenanzahl = buch.getSeitenanzahl();
        this.verlag = buch.getVerlag();
    }

}
