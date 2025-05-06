package com.backend.entity;

import jakarta.persistence.*;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "filiale")
public class Filiale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "filial_id")
    private Long filialId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "anschrift", nullable = false)
    private String anschrift;

    @OneToMany(mappedBy = "filiale", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Angebot> angebote;
}
