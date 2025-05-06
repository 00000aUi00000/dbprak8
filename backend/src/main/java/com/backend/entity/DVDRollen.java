package com.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "dvdrollen")
@IdClass(DVDRollenId.class)
public class DVDRollen {

    @Id
    @ManyToOne
    @JoinColumn(name = "produkt_id", nullable = false)
    private DVD dvd;

    @Id
    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Id
    @Column(name = "rolle", nullable = false)
    private String rolle;
}
