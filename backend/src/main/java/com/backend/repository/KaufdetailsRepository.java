package com.backend.repository;

import com.backend.entity.KaufDetails;
import com.backend.entity.KaufdetailsId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KaufdetailsRepository extends JpaRepository<KaufDetails, KaufdetailsId> {
}
