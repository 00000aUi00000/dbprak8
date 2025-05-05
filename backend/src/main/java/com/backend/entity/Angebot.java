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
    private Integer angebotsId;
    private Integer filialeId;
    private String produktId;


}
