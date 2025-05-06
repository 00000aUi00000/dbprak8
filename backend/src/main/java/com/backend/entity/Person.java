package com.backend.entity;

import jakarta.persistence.*;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    private Long personId;

    @Column(name = "name", nullable = false)
    private String name;

    // Rückverweise

    @OneToMany(mappedBy = "person")
    private Set<Autoren> autorBeitraege;

    @OneToMany(mappedBy = "hauptautor")
    private Set<Autoren> hauptautorBeitraege;

    @OneToMany(mappedBy = "person")
    private Set<Kuenstler> kuenstlerBeitraege;

    @OneToMany(mappedBy = "person")
    private Set<DVDRollen> dvdrollenBeitraege;
}
