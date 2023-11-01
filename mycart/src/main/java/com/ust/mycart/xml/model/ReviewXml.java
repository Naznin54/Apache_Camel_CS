package com.ust.mycart.xml.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"reviewrating", "reviewcomment"})
public class ReviewXml {
	
	private String reviewrating;
    private String reviewcomment;

 

	public ReviewXml(String reviewrating, String reviewcomment) {
        this.reviewrating = reviewrating;
        this.reviewcomment = reviewcomment;
    }

    @XmlElement(name = "reviewrating")
    public String getReviewrating() {
        return reviewrating;
    }

    public void setReviewrating(String reviewrating) {
        this.reviewrating = reviewrating;
    }

    @XmlElement(name = "reviewcomment")
    public String getReviewcomment() {
        return reviewcomment;
    }

    public void setReviewcomment(String reviewcomment) {
        this.reviewcomment = reviewcomment;
    }
}


