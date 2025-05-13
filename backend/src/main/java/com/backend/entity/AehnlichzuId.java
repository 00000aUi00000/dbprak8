package com.backend.entity;

import java.io.Serializable;
import java.util.Objects;

public class AehnlichzuId implements Serializable {

    private String produktA;
    private String produktB;

    public AehnlichzuId() {}

    public AehnlichzuId(String produktA, String produktB) {
        this.produktA = produktA;
        this.produktB = produktB;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AehnlichzuId)) return false;
        AehnlichzuId that = (AehnlichzuId) o;
        return Objects.equals(produktA, that.produktA) &&
               Objects.equals(produktB, that.produktB);
    }

    @Override
    public int hashCode() {
        return Objects.hash(produktA, produktB);
    }

}
