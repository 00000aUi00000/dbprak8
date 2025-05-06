package com.backend.repository;

import com.backend.entity.Kaufdetails;
import com.backend.entity.KaufdetailsId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KaufdetailsRepository extends JpaRepository<Kaufdetails, KaufdetailsId> {
}
