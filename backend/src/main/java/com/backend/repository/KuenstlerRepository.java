package com.backend.repository;

import com.backend.entity.Kuenstler;
import com.backend.entity.KuenstlerId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KuenstlerRepository extends JpaRepository<Kuenstler, KuenstlerId> {
}
