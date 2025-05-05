package main.java.com.backend.entity;

import jakarta.persistence.*;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "angebotdeatils")
public class AngebotDetails {

    @Id    
    private Integer angebotsId;
    private Double preis;
    private String zustand;


}
