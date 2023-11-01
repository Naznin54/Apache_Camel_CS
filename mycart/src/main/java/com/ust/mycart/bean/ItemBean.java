package com.ust.mycart.bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.camel.Exchange;
import org.bson.Document;
import org.springframework.stereotype.Component;

import com.ust.mycart.constants.ApplicationConstants;
import com.ust.mycart.model.Item;
import com.ust.mycart.model.ItemPrice;
import com.ust.mycart.model.StockDetails;
import com.ust.mycart.response.model.CategoryResponse;
import com.ust.mycart.response.model.ItemResponse;

@Component
public class ItemBean {
	
	public void ItemDetailsResponse(Exchange exchange) {
		
		Document itemDocument = exchange.getIn().getBody(Document.class);
        
        String categoryName = exchange.getProperty("categoryName", String.class);

        ItemResponse itemResponse = new ItemResponse();
        itemResponse.setId(itemDocument.getString("_id"));
        itemResponse.setItemName(itemDocument.getString("itemName"));
        itemResponse.setCategoryName(categoryName);

        Document itemPriceDocument = itemDocument.get("itemPrice", Document.class);
        
        ItemPrice itemPrice = new ItemPrice();
        itemPrice.setBasePrice(itemPriceDocument.getDouble("basePrice"));
        itemPrice.setSellingPrice(itemPriceDocument.getDouble("sellingPrice"));
        
        itemResponse.setItemPrice(itemPrice);

        Document stockDetailsDocument = itemDocument.get("stockDetails", Document.class);
        
        StockDetails stockDetails = new StockDetails();
        stockDetails.setAvailableStock(stockDetailsDocument.getInteger("availableStock"));
        stockDetails.setUnitOfMeasure(stockDetailsDocument.getString("unitOfMeasure"));
        
        itemResponse.setStockDetails(stockDetails);

        itemResponse.setSpecialProduct(itemDocument.getBoolean("specialProduct"));

        exchange.getIn().setBody(itemResponse);
	}
	
	

	    public void categoryNameDeptCriteria(Exchange exchange) {
	    	
	    	String categoryId = exchange.getProperty(ApplicationConstants.CATEGORY_ID, String.class);
    	
	        String includeSpecialParam = exchange.getIn().getHeader("includeSpecial", String.class);
	        boolean includeSpecial = Boolean.parseBoolean(includeSpecialParam);
	        
	        String criteria;
	        if (includeSpecial) {
	            criteria = "{\"categoryId\": '" + categoryId + "', \"specialProduct\": true}";
	        }
	        else {
	            criteria = "{\"categoryId\": '" + categoryId + "'}";
	        }

	        exchange.getIn().setHeader(ApplicationConstants.MONGO_DB_CRITERIA, criteria);
	    }
	

	
	public void CategoryNameDeptResponse(Exchange exchange) {
		
		    String categoryName = exchange.getProperty("categoryName", String.class);
	        String categoryDep = exchange.getProperty("categoryDep", String.class);

	       
			List<Document> items = exchange.getIn().getBody(List.class);

	        CategoryResponse categoryRes = new CategoryResponse();
	        categoryRes.setCategoryName(categoryName);
	        categoryRes.setCategoryDepartment(categoryDep);
	        categoryRes.setItems(items);

	        exchange.getIn().setBody(categoryRes);
	}
	
	
	public void addNewItem(Exchange exchange) {
		

    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String lastUpdateDate = dateFormat.format(new Date());

        Item newItem = exchange.getIn().getBody(Item.class);
        newItem.setLastUpdateDate(lastUpdateDate);       
        exchange.getIn().setBody(newItem);
		
	}
	
	public void updateItem(Exchange exchange) {
		
		 Document itemDocument = exchange.getIn().getBody(Document.class);

		   int soldOut = exchange.getProperty("soldOut", Integer.class);
		   int damaged = exchange.getProperty("damaged", Integer.class);
		   	    
		   Document stockDetails = itemDocument.get("stockDetails", Document.class);
	 
			int currentAvailableStock = stockDetails.getInteger("availableStock", 0);
			
			int newAvailableStock = currentAvailableStock - soldOut - damaged;
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    String lastupdatedate = dateFormat.format(new Date());
			itemDocument.put("lastUpdateDate", lastupdatedate);
			
			stockDetails.put("availableStock", newAvailableStock);
			
			itemDocument.put("stockDetails", stockDetails);
			exchange.getIn().setBody(itemDocument);
		
	}

}
