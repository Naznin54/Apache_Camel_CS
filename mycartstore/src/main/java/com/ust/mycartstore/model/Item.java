package com.ust.mycartstore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Item {

	@JsonProperty("id")
	private String id;

	@JsonProperty("itemName")
	private String itemName;

	@JsonProperty("categoryName")
	private String categoryName;

	@JsonIgnore
	@JsonProperty("itemPrice")
	private ItemPrice itemPrice;

	@JsonIgnore
	@JsonProperty("stockDetails")
	private StockDetails stockDetails;

	@JsonIgnore
	@JsonProperty("specialProduct")
	private boolean specialProduct;

	public Item() {
		super();

	}

	public Item(String id, String itemName, String categoryName, ItemPrice itemPrice, StockDetails stockDetails,
			boolean specialProduct) {
		super();
		this.id = id;
		this.itemName = itemName;
		this.categoryName = categoryName;
		this.itemPrice = itemPrice;
		this.stockDetails = stockDetails;
		this.specialProduct = specialProduct;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
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

}
