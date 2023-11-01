package com.ust.mycart.xml.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "Reviews")
public class ReviewsXml {

    private List<ItemXml> items;

    public ReviewsXml() {
        
    }

    public ReviewsXml(List<ItemXml> items) {
        this.items = items;
    }

    @XmlElement(name = "item")
    public List<ItemXml> getItems() {
        return items;
    }

    public void setItems(List<ItemXml> items) {
        this.items = items;
    }
}



