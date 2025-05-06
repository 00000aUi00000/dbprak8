package com.backend.repository;

import com.backend.entity.Angebot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AngebotRepository extends JpaRepository<Angebot, Long> {
}
