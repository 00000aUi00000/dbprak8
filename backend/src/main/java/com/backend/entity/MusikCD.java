package com.backend.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "musikcd")
@PrimaryKeyJoinColumn(name = "produkt_id")
public class MusikCD extends Produkt {

    // @Column(name = "erscheinungsdatum")
    // private LocalDate erscheinungsdatum;

    @Column(name = "anzahlCDs")
    private Integer anzahlcds;

    @OneToMany(mappedBy = "musikCD", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Kuenstler> kuenstler;

    @OneToMany(mappedBy = "musikCD", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Labelliste> labelliste;

    @OneToMany(mappedBy = "musikCD", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Trackliste> trackliste;

    public boolean addKuenstler(Kuenstler kuenstler) {
        if (getKuenstler() == null)
            setKuenstler(new HashSet<>());
        return getKuenstler().add(kuenstler);
    }

    public boolean addLabelliste(Labelliste labelliste) {
        if (getLabelliste() == null)
            setLabelliste(new HashSet<>());
        return getLabelliste().add(labelliste);
    }

    public boolean addTrackliste(Trackliste trackliste) {
        if (getTrackliste() == null)
            setTrackliste(new HashSet<>());
        return getTrackliste().add(trackliste);
    }

}
