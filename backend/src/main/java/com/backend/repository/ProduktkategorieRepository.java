package com.backend.repository;

import com.backend.entity.Produktkategorie;
import com.backend.entity.ProduktkategorieId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduktkategorieRepository extends JpaRepository<Produktkategorie, ProduktkategorieId> {

    List<Produktkategorie> findByProdukt_ProduktId(String produktId);

    List<Produktkategorie> findByKategorie_KategorieId(Long kategorieId);

    List<Produktkategorie> findByHauptkategorie_KategorieId(Long hauptkategorieId);
}
