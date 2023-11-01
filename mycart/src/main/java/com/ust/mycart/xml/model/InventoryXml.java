package com.ust.mycart.xml.model;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "inventory")
public class InventoryXml {
	
	private List<CategoryXml> categoriesList;

	public InventoryXml(List<CategoryXml> categoriesList) {
		super();
		this.categoriesList = categoriesList;
	}
		

	public InventoryXml() {
		super();
		
	}


	@XmlElement(name = "category")
	public List<CategoryXml> getCategoriesList() {
		return categoriesList;
	}

	public void setCategoriesList(List<CategoryXml> categoriesList) {
		this.categoriesList = categoriesList;
	}
	
	

}
