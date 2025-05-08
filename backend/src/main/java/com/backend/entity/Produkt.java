package com.backend.entity;

import jakarta.persistence.*;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "produkt", indexes = {
    @Index(name = "idx_produkt_verkaufsrang", columnList = "verkaufsrang"),
    @Index(name = "idx_produkt_rating", columnList = "rating")
})
public class Produkt {

    @Id
    @Column(name = "produkt_id")
    private String produktId;

    @Column(name = "titel", nullable = false)
    private String titel;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "verkaufsrang")
    private Integer verkaufsrang;

    @Column(name = "bild", length=1023)
    private String bild;

    // Beziehungen 

    @OneToMany(mappedBy = "produkt", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Rezension> rezensionen;

    @OneToMany(mappedBy = "produkt", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Angebot> angebote;

    @OneToMany(mappedBy = "produktA", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AehnlichZu> aehnlicheProdukte;

    @OneToMany(mappedBy = "produktB", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AehnlichZu> aehnlichVon;

    @ManyToMany
    @JoinTable(
        name = "produktkategorie",
        joinColumns = @JoinColumn(name = "produkt_id"),
        inverseJoinColumns = @JoinColumn(name = "kategorie_id")
    )
    private Set<Kategorie> kategorien;
}
