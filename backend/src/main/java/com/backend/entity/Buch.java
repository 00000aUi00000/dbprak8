package main.java.com.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "buch")
@PrimaryKeyJoinColumn(name = "produkt_id")
public class Buch extends Produkt {

    @Column(name = "isbn", nullable = false, unique = true)
    private String isbn;

    @Column(name = "seitenanzahl")
    private Integer seitenanzahl;

    @Column(name = "erscheinungsdatum")
    private LocalDate erscheinungsdatum;

    @Column(name = "verlag")
    private String verlag;
}
