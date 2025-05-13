package com.backend.entity;

import java.io.Serializable;
import java.util.Objects;

public class KategorieHierarchieId implements Serializable {

    private Long parent;
    private Long child;

    public KategorieHierarchieId() {}

    public KategorieHierarchieId(Long parent, Long child) {
        this.parent = parent;
        this.child = child;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KategorieHierarchieId that)) return false;
        return Objects.equals(parent, that.parent) && Objects.equals(child, that.child);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, child);
    }
}
