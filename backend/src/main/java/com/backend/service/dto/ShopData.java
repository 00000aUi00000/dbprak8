package com.backend.service.dto;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@XmlRootElement(name = "shop")
@XmlAccessorType(XmlAccessType.FIELD)
public class ShopData {

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "street")
    private String street;

    @XmlAttribute(name = "zip")
    private String zip;

    @XmlElement(name = "item")
    private List<ItemData> items;
}
