package main.java.com.backend.entity;

import jakarta.persistence.*;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "produktkategorie")
public class ProduktKategorie {

    @Id
    private Integer kategorieId;
    private String produktId;


}
