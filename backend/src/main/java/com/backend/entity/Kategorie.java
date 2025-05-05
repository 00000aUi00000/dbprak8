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
    private Integer kategorieId;
    private String name;
    private Integer parentKategorie;


}
