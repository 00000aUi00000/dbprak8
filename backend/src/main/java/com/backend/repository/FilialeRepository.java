package com.backend.repository;

import com.backend.entity.Filiale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FilialeRepository extends JpaRepository<Filiale, Long> {
    Optional<Filiale> findByNameAndAnschrift(String name, String anschrift);
}
