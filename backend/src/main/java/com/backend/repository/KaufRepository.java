package com.backend.repository;

import com.backend.entity.Kauf;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KaufRepository extends JpaRepository<Kauf, Long> {
}
