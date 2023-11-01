package com.ust.mycart.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {
	
	    @JsonProperty("_id")
	    private String itemId;
	    @JsonProperty("itemName")
	    private String itemName;
	    @JsonProperty("categoryId")
	    private String categoryId;
	    @JsonProperty("lastUpdateDate")
	    private String lastUpdateDate;
	    @JsonProperty("itemPrice")
	    private ItemPrice itemPrice;
	    @JsonProperty("stockDetails")
	    private StockDetails stockDetails;
	    @JsonProperty("specialProduct")
	    private boolean specialProduct;
	    @JsonProperty("review")
	    private List<Review> review;
		public Item(String itemId, String itemName, String categoryId, String lastUpdateDate, ItemPrice itemPrice,
				StockDetails stockDetails, boolean specialProduct, List<Review> review) {
			super();
			this.itemId = itemId;
			this.itemName = itemName;
			this.categoryId = categoryId;
			this.lastUpdateDate = lastUpdateDate;
			this.itemPrice = itemPrice;
			this.stockDetails = stockDetails;
			this.specialProduct = specialProduct;
			this.review = review;
		}
		public Item() {
			super();
			
		}
		public String getItemId() {
			return itemId;
		}
		public void setItemId(String itemId) {
			this.itemId = itemId;
		}
		public String getItemName() {
			return itemName;
		}
		public void setItemName(String itemName) {
			this.itemName = itemName;
		}
		public String getCategoryId() {
			return categoryId;
		}
		public void setCategoryId(String categoryId) {
			this.categoryId = categoryId;
		}
		public String getLastUpdateDate() {
			return lastUpdateDate;
		}
		public void setLastUpdateDate(String lastUpdateDate) {
			this.lastUpdateDate = lastUpdateDate;
		}
		public ItemPrice getItemPrice() {
			return itemPrice;
		}
		public void setItemPrice(ItemPrice itemPrice) {
			this.itemPrice = itemPrice;
		}
		public StockDetails getStockDetails() {
			return stockDetails;
		}
		public void setStockDetails(StockDetails stockDetails) {
			this.stockDetails = stockDetails;
		}
		public boolean isSpecialProduct() {
			return specialProduct;
		}
		public void setSpecialProduct(boolean specialProduct) {
			this.specialProduct = specialProduct;
		}
		public List<Review> getReview() {
			return review;
		}
		public void setReview(List<Review> review) {
			this.review = review;
		}
		
		
}
	