package com.backend.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

    public boolean addAngebot(Angebot angebot) {
        if (getAngebote() == null)
            setAngebote(new HashSet<>());
        return getAngebote().add(angebot);
    }

}
