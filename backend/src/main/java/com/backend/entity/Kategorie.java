package com.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "kategorie", indexes = {
    @Index(name = "idx_kategorie_pfad", columnList = "pfad")
})
public class Kategorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kategorie_id")
    private Long kategorieId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "pfad", nullable = false, unique = true, length = 1023)
    private String pfad; // z.B. "Musik > Rock > Classic Rock"

    @ManyToMany(mappedBy = "kategorien")
    private Set<Produkt> produkte;
}
