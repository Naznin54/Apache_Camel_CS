package com.ust.mycart.response.model;

import java.util.List;

import org.bson.Document;

public class CategoryResponse {
	private String categoryName;
    private String categoryDepartment;
    private List<Document> items;
    
	public CategoryResponse(String categoryName, String categoryDepartment, List<Document> items) {
		super();
		this.categoryName = categoryName;
		this.categoryDepartment = categoryDepartment;
		this.items = items;
	}
	public CategoryResponse() {
		super();
		
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getCategoryDepartment() {
		return categoryDepartment;
	}
	public void setCategoryDepartment(String categoryDepartment) {
		this.categoryDepartment = categoryDepartment;
	}
	public List<Document> getItems() {
		return items;
	}
	public void setItems(List<Document> items) {
		this.items = items;
	}
	
    
    
}
