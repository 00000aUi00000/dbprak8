package com.backend.repository;

import com.backend.entity.Produkt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProduktRepository extends JpaRepository<Produkt, String> {
}
