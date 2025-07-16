package com.backend.repository;

import com.backend.entity.Rezension;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RezensionRepository extends JpaRepository<Rezension, Long> {

    @Query("SELECT AVG(r.punkte) FROM Rezension r WHERE r.kunde.kundeId = :kundeId")
    Double averageRatingByKundeId(@Param("kundeId") Long kundeId);
}
