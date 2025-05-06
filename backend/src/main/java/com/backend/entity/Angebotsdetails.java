package com.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "angebotsdetails", indexes = @Index(name = "idx_angebotdetails_zustand", columnList="zustand"))
@IdClass(AngebotsdetailsId.class)
public class Angebotsdetails {

    @Id
    @ManyToOne
    @JoinColumn(name = "angebot_id", nullable = false)
    private Angebot angebot;

    @Id
    @Column(name = "zustand", nullable = false)
    private String zustand;

    @Column(name = "preis")
    private Double preis;
}
