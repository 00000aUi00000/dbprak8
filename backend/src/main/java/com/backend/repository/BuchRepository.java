package com.backend.repository;

import com.backend.entity.Buch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuchRepository extends JpaRepository<Buch, String> {
}
