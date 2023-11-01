package com.ust.mycart.model;

public class Review {
	private String rating;
    private String comment;
    
	public Review(String rating, String comment) {
		super();
		this.rating = rating;
		this.comment = comment;
	}
	
	
	public Review() {
		super();
		
	}


	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
    

}
