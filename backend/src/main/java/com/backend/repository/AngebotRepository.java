package com.backend.repository;

import com.backend.entity.Angebot;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AngebotRepository extends JpaRepository<Angebot, Long> {

    @Query("SELECT a FROM Angebot a WHERE a.filiale.filialId = ?1 AND a.produkt.produktId = ?2")
    Optional<Angebot> findByFilialeAndProdukt(Long filialId, String produktId);

}
