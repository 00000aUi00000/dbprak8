package com.backend.repository;

import com.backend.entity.Produkt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Set;
import java.util.Optional;

public interface ProduktRepository extends JpaRepository<Produkt, String> {
    Optional<Produkt> findByProduktId(String produktId);
    @Query("SELECT p.produktId FROM Produkt p")
    Set<String> findAllProduktIds();  // → Lädt alle Produkt-IDs als Set
}
