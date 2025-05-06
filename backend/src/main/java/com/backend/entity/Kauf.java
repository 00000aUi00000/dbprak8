package com.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "kauf")
public class Kauf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kauf_id")
    private Long kaufId;

    @ManyToOne
    @JoinColumn(name = "kunde_id", nullable = false)
    private Kunde kunde;

    @Column(name = "kaufdatum", nullable = false)
    private LocalDateTime kaufdatum;

    @Column(name = "lieferadresse", nullable = false)
    private String lieferadresse;

    @Column(name = "kontonummer", nullable = false)
    private String kontonummer;

    @OneToMany(mappedBy = "kauf", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<KaufDetails> kaufdetails;
}
