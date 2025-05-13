package com.backend.service.dto;

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
    private ItemValue item;

    @XmlElement(name = "sim_product")
    private SimProductValue product;

    public String getAsin() {
        final boolean isSecondVariant = item == null || item.getAsin() == null || item.getAsin().isBlank();

        return isSecondVariant ? (product != null ? product.getAsin() : null) : item.getAsin();
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
