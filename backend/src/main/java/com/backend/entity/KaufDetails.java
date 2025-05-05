package main.java.com.backend.entity;

import jakarta.persistence.*;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "kaufdetails")
public class KaufDetails {

    @Id    
    private Integer angebotsId;
    private Integer bestellId;
    private String zustand;
    private Integer menge;
    private Double preis;


}
