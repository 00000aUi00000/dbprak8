package com.backend.entity;

import java.io.Serializable;
import java.util.Objects;

public class KuenstlerId implements Serializable {

    private String musikCD;
    private Long person;

    public KuenstlerId() {}

    public KuenstlerId(String musikCD, Long person) {
        this.musikCD = musikCD;
        this.person = person;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KuenstlerId)) return false;
        KuenstlerId that = (KuenstlerId) o;
        return Objects.equals(musikCD, that.musikCD) &&
               Objects.equals(person, that.person);
    }

    @Override
    public int hashCode() {
        return Objects.hash(musikCD, person);
    }
}
