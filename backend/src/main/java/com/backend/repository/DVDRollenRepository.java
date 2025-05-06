package com.backend.repository;

import com.backend.entity.DVDRollen;
import com.backend.entity.DVDRollenId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DVDRollenRepository extends JpaRepository<DVDRollen, DVDRollenId> {
}
