package main.java.com.backend.entity;

import jakarta.persistence.*;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "produkt")
public class Produkt {

    @Id
    @Column(name = "produkt_id", nullable = false)
    private String produktId;
    private String titel;
    private Double rating;
    @Column(name = "verkaufsrang")
    private Integer verkaufsrang;
    private String bild;
    

    // Beziehungen 

    // @OneToMany(mappedBy = "produkt", cascade = CascadeType.ALL)
    // private Set<Rezession> rezessionen;

    // @OneToMany(mappedBy = "produkt", cascade = CascadeType.ALL)
    // private Set<Angebot> angebote;

    // @OneToMany(mappedBy = "produkt", cascade = CascadeType.ALL)
    // private Set<ProduktKategorie> kategorien;

    // @OneToMany(mappedBy = "produkt", cascade = CascadeType.ALL)
    // private Set<AehnlichZu> aehnlichZu;

    // @OneToMany(mappedBy = "produkt", cascade = CascadeType.ALL)
    // private Set<AehnlichZu> aehnlichVon;  // Umkehrseite der Beziehung

    // // Vererbung: Buch, DVD, MusikCD => Single Table oder Table per Subclass Mapping
    // // Beispiel für JOINED:
    // @OneToOne(mappedBy = "produkt", cascade = CascadeType.ALL)
    // private Buch buch;

    // @OneToOne(mappedBy = "produkt", cascade = CascadeType.ALL)
    // private DVD dvd;

    // @OneToOne(mappedBy = "produkt", cascade = CascadeType.ALL)
    // private MusikCD musikCD;
    
}
