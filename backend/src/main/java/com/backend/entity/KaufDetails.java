package com.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "kaufdetails")
@IdClass(KaufdetailsId.class)
public class KaufDetails {

    @Id
    @ManyToOne
    @JoinColumn(name = "kauf_id", nullable = false)
    private Kauf kauf;

    @Id
    @ManyToOne
    @JoinColumn(name = "angebot_id", nullable = false)
    private Angebot angebot;

    @Id
    @Column(name = "zustand", nullable = false)
    private String zustand;

    @Column(name = "menge", nullable = false)
    private Integer menge;

    @Column(name = "preis", nullable = false)
    private Double preis; 
}
