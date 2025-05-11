package com.backend.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.Check;

import jakarta.persistence.*;
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
@Check(constraints = "verkaufsrang >= 0")
@Check(constraints = "rating >= 0")
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

    public boolean addRezension(Rezension rezension) {
        if(getRezensionen() == null)
            setRezensionen(new HashSet<>());
        return getRezensionen().add(rezension);
    }

    public boolean addAngebot(Angebot angebot) {
        if(getAngebote() == null)
            setAngebote(new HashSet<>());
        return getAngebote().add(angebot);
    }

    public boolean addAehnlichProdukte(AehnlichZu aehnlichZu) {
        if(getAehnlicheProdukte() == null)
            setAehnlicheProdukte(new HashSet<>());
        return getAehnlicheProdukte().add(aehnlichZu);
    }

    public boolean addAenlichZu(AehnlichZu aehnlichZu) {
        if(getAehnlichVon() == null)
            setAehnlichVon(new HashSet<>());
        return getAehnlichVon().add(aehnlichZu);
    }

}
