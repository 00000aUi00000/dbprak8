package com.backend.entity;

import java.io.Serializable;
import java.util.Objects;

public class KaufdetailsId implements Serializable {

    private Long kauf;
    private Long angebot;
    private String zustand;

    public KaufdetailsId() {}

    public KaufdetailsId(Long kauf, Long angebot, String zustand) {
        this.kauf = kauf;
        this.angebot = angebot;
        this.zustand = zustand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KaufdetailsId)) return false;
        KaufdetailsId that = (KaufdetailsId) o;
        return Objects.equals(kauf, that.kauf) &&
               Objects.equals(angebot, that.angebot) &&
               Objects.equals(zustand, that.zustand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kauf, angebot, zustand);
    }
}
