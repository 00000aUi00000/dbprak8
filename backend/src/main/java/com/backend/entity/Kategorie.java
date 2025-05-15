package com.backend.entity;

import jakarta.persistence.*;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "kategorie")
public class Kategorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kategorie_id")
    private Long kategorieId;

    @Column(name = "name", nullable = false) // Name laut Aufgabenstellung nicht eindeutig
    private String name;

    // Beziehung zu Produkten bleibt wie gehabt
    @ManyToMany(mappedBy = "kategorien")
    private Set<Produkt> produkte;
}
