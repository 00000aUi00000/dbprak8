package com.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "kategorie_hierarchie", indexes = {
    @Index(name = "idx_kategoriehierarchie_parent", columnList = "parent_id"),
    @Index(name = "idx_kategoriehierarchie_child", columnList = "child_id")
})
@IdClass(KategorieHierarchieId.class)
public class KategorieHierarchie {

    @Id
    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false)
    private Kategorie parent;

    @Id
    @ManyToOne
    @JoinColumn(name = "child_id", nullable = false)
    private Kategorie child;
}
