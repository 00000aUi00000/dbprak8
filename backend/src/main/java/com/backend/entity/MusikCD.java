package main.java.com.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "musikcd")
@PrimaryKeyJoinColumn(name = "produkt_id")
public class MusikCD extends Produkt {

    @Column(name = "label")
    private String label;

    @Column(name = "erscheinungsdatum")
    private LocalDate erscheinungsdatum;

    @OneToMany(mappedBy = "musikCD", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Kuenstler> kuenstler;

    @OneToMany(mappedBy = "musikCD", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Trackliste> trackliste;
}
