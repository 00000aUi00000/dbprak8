package main.java.com.backend.entity;

import jakarta.persistence.*;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "angebot")
public class Angebot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "angebot_id")
    private Long angebotId;

    @ManyToOne
    @JoinColumn(name = "produkt_id", nullable = false)
    private Produkt produkt;

    @ManyToOne
    @JoinColumn(name = "filial_id", nullable = false)
    private Filiale filiale;

    @OneToMany(mappedBy = "angebot", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Angebotsdetails> angebotsdetails;

}
