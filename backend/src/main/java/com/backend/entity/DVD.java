package com.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "dvd")
@PrimaryKeyJoinColumn(name = "produkt_id")
public class DVD extends Produkt {

    @Column(name = "format", nullable = false)
    private String format;

    @Column(name = "laufzeit")
    private Integer laufzeit;  // Minuten

    @Column(name = "region_code")
    private Integer regionCode;
}
