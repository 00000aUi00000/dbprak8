package com.backend.repository;

import com.backend.entity.Produkt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProduktRepository extends JpaRepository<Produkt, String> {
    Optional<Produkt> findByProduktId(String produktId);
}
