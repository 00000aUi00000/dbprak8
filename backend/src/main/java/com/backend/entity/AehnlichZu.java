package main.java.com.backend.entity;

import jakarta.persistence.*;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "aehnlichzu")
public class AehnlichZu {

    @Id    
    private String produktIda;
    private String produktIdb;

}
