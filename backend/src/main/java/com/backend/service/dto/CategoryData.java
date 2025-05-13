package com.backend.service.dto;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class CategoryData {

    @XmlElement(name = "category")
    private List<CategoryData> subcategories = new ArrayList<>();

    @XmlElement(name = "item")
    private List<String> items = new ArrayList<>();

    @XmlMixed
    private List<String> nameContent;

    public String getName() {
        if (nameContent != null) {
            return nameContent.stream().filter(s -> s != null && !s.trim().isEmpty()).findFirst().orElse(null);
        }
        return null;
    }

    public List<CategoryData> getSubcategories() {
        return subcategories;
    }

    public List<String> getItems() {
        return items;
    }
}


