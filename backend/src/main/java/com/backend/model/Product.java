package com.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Product {
    @Id
    private String asin;
    private String title;

    // Konstruktoren
    public Product() {}
    public Product(String asin, String title) {
        this.asin = asin;
        this.title = title;
    }

    // Getter & Setter
    public String getAsin() {
        return this.asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
