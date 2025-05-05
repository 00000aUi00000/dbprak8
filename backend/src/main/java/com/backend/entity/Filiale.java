package main.java.com.backend.entity;

import jakarta.persistence.*;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "filiale")
public class Filiale {

    @Id    
    private Integer filialeId;
    private String name;
    private String anschrift;


}
