package com.backend.entity;

import java.io.Serializable;
import java.util.Objects;

public class AutorenId implements Serializable {

    private String produkt;
    private Long person;

    public AutorenId() {}

    public AutorenId(String produkt, Long person) {
        this.produkt = produkt;
        this.person = person;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AutorenId)) return false;
        AutorenId that = (AutorenId) o;
        return Objects.equals(produkt, that.produkt) &&
               Objects.equals(person, that.person);
    }

    @Override
    public int hashCode() {
        return Objects.hash(produkt, person);
    }
}
