package main.java.com.backend.entity;

import jakarta.persistence.*;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rezesion")
public class Rezesion {

    @Id    
    private Integer rezesionId;
    private String produktId;
    private Integer kundenId;
    private Integer anzahlNuetzlich;
    private Integer punktZahl;
    private String zusammenfassung;
    private String inhalt;
    // Typ ändern auf date
    private String datum;

}
