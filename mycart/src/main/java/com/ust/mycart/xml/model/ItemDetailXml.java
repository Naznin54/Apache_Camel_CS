package com.ust.mycart.xml.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "itemId", "categoryId", "availableStock", "sellingPrice" })
public class ItemDetailXml {

	private String itemId;

	private String categoryId;

	private int availableStock;

	private double sellingPrice;

	public ItemDetailXml(String itemId, String categoryId, int availableStock, double sellingPrice) {
		super();
		this.itemId = itemId;
		this.categoryId = categoryId;
		this.availableStock = availableStock;
		this.sellingPrice = sellingPrice;
	}

	@XmlElement(name = "itemId")
	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	@XmlElement(name = "categoryId")
	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	@XmlElement(name = "availableStock")
	public int getAvailableStock() {
		return availableStock;
	}

	public void setAvailableStock(int availableStock) {
		this.availableStock = availableStock;
	}

	@XmlElement(name = "sellingPrice")
	public double getSellingPrice() {
		return sellingPrice;
	}

	public void setSellingPrice(double sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

}
