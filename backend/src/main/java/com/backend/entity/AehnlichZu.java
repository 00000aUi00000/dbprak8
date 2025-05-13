package com.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "aehnlichzu")
@IdClass(AehnlichzuId.class)
public class AehnlichZu {

    @Id
    @ManyToOne
    @JoinColumn(name = "produkt_ida", referencedColumnName = "produkt_id")
    private Produkt produktA;

    @Id
    @ManyToOne
    @JoinColumn(name = "produkt_idb", referencedColumnName = "produkt_id")
    private Produkt produktB;
    
}
