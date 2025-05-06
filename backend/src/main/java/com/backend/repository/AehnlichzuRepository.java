package com.backend.repository;

import com.backend.entity.AehnlichZu;
import com.backend.entity.AehnlichzuId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AehnlichzuRepository extends JpaRepository<AehnlichZu, AehnlichzuId> {
}
