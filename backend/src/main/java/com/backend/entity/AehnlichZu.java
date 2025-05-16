package com.backend.entity;

import org.hibernate.annotations.Check;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "aehnlichzu", indexes = {
    @Index(name = "idx_aehnlichzu_ba", columnList = "produkt_idb, produkt_ida")
})
@Check(constraints = "produkt_ida <> produkt_idb")
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
