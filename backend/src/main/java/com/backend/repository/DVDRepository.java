package com.backend.repository;

import com.backend.entity.DVD;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DVDRepository extends JpaRepository<DVD, String> {
}
