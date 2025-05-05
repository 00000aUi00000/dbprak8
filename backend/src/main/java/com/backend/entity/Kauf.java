package main.java.com.backend.entity;

import jakarta.persistence.*;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "kauf")
public class Kauf {

    @Id    
    private Integer bestellId;
    private Integer kundenId;
    //Typ muss noch geändert werden
    private String timestamp;
    private String kontonr;
    private String lieferanschrift;

}
