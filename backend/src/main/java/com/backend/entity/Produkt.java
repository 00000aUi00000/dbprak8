package com.backend.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
@Check(constraints = "rating BETWEEN 1 AND 5")
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

    @Column(name = "erscheinungsdatum")
    private LocalDate erscheinungsdatum;    

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

    public Set<Kategorie> getKategorien() {
        return kategorien != null ? kategorien : Set.of();
    }

    public Set<Kategorie> getHauptkategorien() {
        if (kategorien == null) return Set.of();
        return kategorien.stream()
            .filter(k -> k.getPfad() != null && !k.getPfad().contains(">"))
            .collect(Collectors.toSet());
    }


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

    public boolean addAehnlichZu(AehnlichZu aehnlichZu) {
        if(getAehnlichVon() == null)
            setAehnlichVon(new HashSet<>());
        return getAehnlichVon().add(aehnlichZu);
    }

}
