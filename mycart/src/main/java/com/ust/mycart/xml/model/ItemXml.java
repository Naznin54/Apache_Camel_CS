package com.ust.mycart.xml.model;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;


	public class ItemXml {

	    private String id;
	    private List<ReviewXml> reviews;
	    		

	    public ItemXml(String id, List<ReviewXml> reviews) {
	        this.id = id;
	        this.reviews = reviews;
	    }

	 
	    @XmlAttribute(name = "id")
	    public String getId() {
	        return id;
	    }

	    public void setId(String id) {
	        this.id = id;
	    }

	    @XmlElement(name = "Review")
	    @XmlElementWrapper(name = "reviews")
	    public List<ReviewXml> getReviews() {
	        return reviews;
	    }

	    public void setReviews(List<ReviewXml> reviews) {
	        this.reviews = reviews;
	    }
	}


