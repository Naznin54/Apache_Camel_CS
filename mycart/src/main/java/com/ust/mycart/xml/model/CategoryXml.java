package com.ust.mycart.xml.model;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

public class CategoryXml {

	private String categoryId;
	private String categoryName;
	private List<ItemDetailXml> itemDetailList;

	public CategoryXml(String categoryId, String categoryName, List<ItemDetailXml> itemDetailList) {
		super();
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.itemDetailList = itemDetailList;
	}
	
	public CategoryXml() {
		super();
		
	}

	@XmlAttribute(name = "id")
	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	@XmlElement(name = "categoryName")
	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	@XmlElement(name = "item")
	public List<ItemDetailXml> getItemDetailList() {
		return itemDetailList;
	}

	public void setItemDetailList(List<ItemDetailXml> itemDetailList) {
		this.itemDetailList = itemDetailList;
	}



}
