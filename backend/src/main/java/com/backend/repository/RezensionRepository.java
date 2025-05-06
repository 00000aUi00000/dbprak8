package com.backend.repository;

import com.backend.entity.Rezension;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RezensionRepository extends JpaRepository<Rezension, Long> {
}
