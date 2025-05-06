package main.java.com.backend.entity;

import java.io.Serializable;
import java.util.Objects;

public class DVDRollenId implements Serializable {

    private String dvd;
    private Long person;
    private String rolle;

    public DVDRollenId() {}

    public DVDRollenId(String dvd, Long person, String rolle) {
        this.dvd = dvd;
        this.person = person;
        this.rolle = rolle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DVDRollenId)) return false;
        DVDRollenId that = (DVDRollenId) o;
        return Objects.equals(dvd, that.dvd) &&
               Objects.equals(person, that.person) &&
               Objects.equals(rolle, that.rolle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dvd, person, rolle);
    }
}
