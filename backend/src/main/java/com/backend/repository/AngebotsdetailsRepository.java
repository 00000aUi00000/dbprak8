package com.backend.repository;

import com.backend.entity.Angebotsdetails;
import com.backend.entity.AngebotsdetailsId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AngebotsdetailsRepository extends JpaRepository<Angebotsdetails, AngebotsdetailsId> {
}
