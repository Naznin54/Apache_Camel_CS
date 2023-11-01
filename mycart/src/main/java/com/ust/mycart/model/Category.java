package com.ust.mycart.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Category {
	
	    @JsonProperty("_id")
	    private String categoryId;
	    @JsonProperty("categoryName")
	    private String categoryName;
	    @JsonProperty("categoryDep")
	    private String categoryDep;
	    @JsonProperty("categoryTax")
	    private double categoryTax;
	    
		public Category(String categoryId, String categoryName, String categoryDep, double categoryTax) {
			super();
			this.categoryId = categoryId;
			this.categoryName = categoryName;
			this.categoryDep = categoryDep;
			this.categoryTax = categoryTax;
		}
		public Category() {
			super();
			
		}
		public String getCategoryId() {
			return categoryId;
		}
		public void setCategoryId(String categoryId) {
			this.categoryId = categoryId;
		}
		public String getCategoryName() {
			return categoryName;
		}
		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}
		public String getCategoryDep() {
			return categoryDep;
		}
		public void setCategoryDep(String categoryDep) {
			this.categoryDep = categoryDep;
		}
		public double getCategoryTax() {
			return categoryTax;
		}
		public void setCategoryTax(double categoryTax) {
			this.categoryTax = categoryTax;
		}
		
	    
		

}
