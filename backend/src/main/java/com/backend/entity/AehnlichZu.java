package main.java.com.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "aehnlichzu")
public class Aehnlichzu {

    @EmbeddedId
    private AehnlichzuId id;

    @MapsId("produktIda")
    @ManyToOne
    @JoinColumn(name = "produkt_ida", referencedColumnName = "produkt_id")
    private Produkt produktA;

    @MapsId("produktIdb")
    @ManyToOne
    @JoinColumn(name = "produkt_idb", referencedColumnName = "produkt_id")
    private Produkt produktB;
}
