package com.backend.service.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class AehnlichData {

    @XmlElement(name = "item")
    private List<ItemValue> items;

    @XmlElement(name = "sim_product")
    private List<SimProductValue> products;

    public List<String> getAsins() {
        final List<String> result = new ArrayList<>();

        if (getItems() != null) {
            for (final ItemValue item : getItems()) {
                if (item != null && item.getAsin() != null && !item.getAsin().isBlank())
                    result.add(item.getAsin());
            }
        }

        if (getProducts() != null) {
            for (final SimProductValue product : getProducts()) {
                if (product != null && product.getAsin() != null && !product.getAsin().isBlank())
                    result.add(product.getAsin());
            }
        }

        return result;
    }

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ItemValue {

        @XmlAttribute(name = "asin")
        private String asin;

    }

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SimProductValue {

        @XmlElement(name = "asin")
        private String asin;

    }

}
