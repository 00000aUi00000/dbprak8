package main.java.com.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "trackliste")
public class Trackliste {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "track_id")
    private Long trackId;

    @ManyToOne
    @JoinColumn(name = "produkt_id", nullable = false)
    private MusikCD musikCD;

    @Column(name = "titel", nullable = false)
    private String titel;
}
