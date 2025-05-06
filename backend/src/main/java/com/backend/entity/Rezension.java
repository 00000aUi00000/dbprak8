package com.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

import org.hibernate.annotations.Check;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rezension", indexes = {
        @Index(name = "idx_rezension_punkte", columnList = "punkte"),
        @Index(name = "idx_rezension_datum", columnList = "datum")
})
@Check(constraints = "punkte between 1 and 5")
public class Rezension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rezension_id")
    private Long rezensionId;

    @ManyToOne
    @JoinColumn(name = "kunde_id", nullable = false)
    private Kunde kunde;

    @ManyToOne
    @JoinColumn(name = "produkt_id", nullable = false)
    private Produkt produkt;

    @Column(name = "punkte", nullable = false)
    private Integer punkte;

    @Column(name = "zusammenfassung")
    private String zusammenfassung;

    @Column(name = "text")
    private String text;

    @Column(name = "datum", nullable = false)
    private LocalDate datum;

    @Column(name = "anzahl_nuetzlich")
    private Integer anzahlNuetzlich;
}
