package main.java.com.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AehnlichzuId implements Serializable {

    @Column(name = "produkt_ida")
    private String produktIda;

    @Column(name = "produkt_idb")
    private String produktIdb;

    public AehnlichzuId() {}

    public AehnlichzuId(String produktIda, String produktIdb) {
        this.produktIda = produktIda;
        this.produktIdb = produktIdb;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AehnlichzuId)) return false;
        AehnlichzuId that = (AehnlichzuId) o;
        return Objects.equals(produktIda, that.produktIda) &&
               Objects.equals(produktIdb, that.produktIdb);
    }

    @Override
    public int hashCode() {
        return Objects.hash(produktIda, produktIdb);
    }
}
