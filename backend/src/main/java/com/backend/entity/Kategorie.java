package main.java.com.backend.entity;

import jakarta.persistence.*;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "kategorie")
public class Kategorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kategorie_id")
    private Long kategorieId;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Kategorie parentKategorie;

    @OneToMany(mappedBy = "parentKategorie", cascade = CascadeType.ALL)
    private Set<Kategorie> unterkategorien;

    @ManyToMany(mappedBy = "kategorien")
    private Set<Produkt> produkte;
}
