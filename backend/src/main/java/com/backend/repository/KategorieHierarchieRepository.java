package com.backend.repository;

import com.backend.entity.Kategorie;
import com.backend.entity.KategorieHierarchie;
import com.backend.entity.KategorieHierarchieId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KategorieHierarchieRepository extends JpaRepository<KategorieHierarchie, KategorieHierarchieId> {

    List<KategorieHierarchie> findByParent(Kategorie parent);

    List<KategorieHierarchie> findByChild(Kategorie child);
}
