package main.java.com.backend.entity;

import jakarta.persistence.*;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "kunde")
public class Kunde {

    @Id    
    private Integer kundenId;
    private String vorname;
    private String nachname;


}
