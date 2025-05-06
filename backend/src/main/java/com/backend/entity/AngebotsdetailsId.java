package com.backend.entity;

import java.io.Serializable;
import java.util.Objects;

public class AngebotsdetailsId implements Serializable {

    private Long angebot;
    private String zustand;

    public AngebotsdetailsId() {}

    public AngebotsdetailsId(Long angebot, String zustand) {
        this.angebot = angebot;
        this.zustand = zustand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AngebotsdetailsId)) return false;
        AngebotsdetailsId that = (AngebotsdetailsId) o;
        return Objects.equals(angebot, that.angebot) &&
               Objects.equals(zustand, that.zustand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(angebot, zustand);
    }
}
