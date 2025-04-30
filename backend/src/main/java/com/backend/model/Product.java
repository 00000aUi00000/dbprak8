package main.java.com.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

// Erzeugt Datenbanktabelle
@Entity
// Lambok Funktion zum automatischen Erstellen der Methoden
@Getter
@Setter
public class Product {
    // Erzeugt Spalten
    @Id
    private String asin;
    private String title;

    // Konstruktoren
    public Product() {}
    public Product(String asin, String title) {
        this.asin = asin;
        this.title = title;
    }

}
