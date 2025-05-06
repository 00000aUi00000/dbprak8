package com.backend.repository;

import com.backend.entity.Autoren;
import com.backend.entity.AutorenId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutorenRepository extends JpaRepository<Autoren, AutorenId> {
}
