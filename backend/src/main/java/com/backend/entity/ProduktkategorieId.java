package com.backend.entity;

import java.io.Serializable;
import java.util.Objects;

public class ProduktkategorieId implements Serializable {
    private String produkt;
    private Long kategorie;

    public ProduktkategorieId() {}

    public ProduktkategorieId(String produkt, Long kategorie) {
        this.produkt = produkt;
        this.kategorie = kategorie;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProduktkategorieId)) return false;
        ProduktkategorieId that = (ProduktkategorieId) o;
        return Objects.equals(produkt, that.produkt) &&
               Objects.equals(kategorie, that.kategorie);
    }

    @Override
    public int hashCode() {
        return Objects.hash(produkt, kategorie);
    }
}
