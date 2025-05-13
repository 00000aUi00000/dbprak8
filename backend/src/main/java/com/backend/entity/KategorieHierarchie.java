package com.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "kategorie_hierarchie")
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
