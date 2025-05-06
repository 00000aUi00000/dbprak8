package com.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "kuenstler")
@IdClass(KuenstlerId.class)
public class Kuenstler {

    @Id
    @ManyToOne
    @JoinColumn(name = "produkt_id", nullable = false)
    private MusikCD musikCD;

    @Id
    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;
}
