package com.backend.entity;

import java.time.LocalDate;

import org.hibernate.annotations.Check;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "dvd")
@Check(constraints = "laufzeit >= 0")
@Check(constraints = "region_code >= 0")
@PrimaryKeyJoinColumn(name = "produkt_id")
public class DVD extends Produkt {

    @Column(name = "format", nullable = false)
    private String format;

    @Column(name = "laufzeit")
    private Integer laufzeit;  // Minuten

    @Column(name = "region_code")
    private Integer regionCode;

    @Column(name = "erscheinungsdatum")
    private LocalDate erscheinungsdatum;
}
