package com.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "produktkategorie")
@IdClass(ProduktkategorieId.class)
public class Produktkategorie {

    @Id
    @ManyToOne
    @JoinColumn(name = "produkt_id", nullable = false)
    private Produkt produkt;

    @Id
    @ManyToOne
    @JoinColumn(name = "kategorie_id", nullable = false)
    private Kategorie kategorie;

    @ManyToOne
    @JoinColumn(name = "hauptkategorie_id")
    private Kategorie hauptkategorie;  //  Hauptkategorie
}
