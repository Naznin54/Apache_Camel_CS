package com.ust.mycart.bean;

import java.util.ArrayList;

import java.util.List;
import org.apache.camel.Exchange;
import org.bson.Document;
import org.springframework.stereotype.Component;

import com.ust.mycart.model.ItemPrice;
import com.ust.mycart.model.StockDetails;
import com.ust.mycart.response.model.ItemResponse;
import com.ust.mycart.xml.model.CategoryXml;
import com.ust.mycart.xml.model.InventoryXml;
import com.ust.mycart.xml.model.ItemDetailXml;
import com.ust.mycart.xml.model.ItemXml;
import com.ust.mycart.xml.model.ReviewXml;
import com.ust.mycart.xml.model.ReviewsXml;

@Component
public class SftpBean {

	public void itemInventoryResponse(Exchange exchange) {

		String categoryId = exchange.getProperty("categoryId", String.class);
		String categoryName = exchange.getProperty("categoryName", String.class);

		List<Document> documentList = exchange.getIn().getBody(List.class);

		List<CategoryXml> categoriesList = new ArrayList<>();

		CategoryXml categoryXml = new CategoryXml();
		categoryXml.setCategoryId(categoryId);
		categoryXml.setCategoryName(categoryName);

		List<ItemDetailXml> filteredItemDetailList = new ArrayList<>();

		for (Document document : documentList) {

			String itemId = document.getString("_id");
			String itemCategoryId = document.getString("categoryId");
			Document stockDetails = (Document) document.get("stockDetails");
			Document itemPrice = (Document) document.get("itemPrice");

			int availableStock = stockDetails.getInteger("availableStock");
			double sellingPrice = itemPrice.getDouble("sellingPrice");

			ItemDetailXml filteredItemDetail = new ItemDetailXml(itemId, itemCategoryId, availableStock, sellingPrice);
			filteredItemDetailList.add(filteredItemDetail);

		}

		if (!filteredItemDetailList.isEmpty()) {

			categoryXml.setItemDetailList(filteredItemDetailList);
			categoriesList.add(categoryXml);
		}

		InventoryXml inventoryXml = new InventoryXml(categoriesList);
		exchange.getIn().setBody(inventoryXml);
	}

	public void reviewDumpResponse(Exchange exchange) {

		List<Document> itemDocuments = exchange.getIn().getBody(List.class);

		List<ItemXml> items = new ArrayList<>();

		for (Document itemDocument : itemDocuments) {

			String itemId = itemDocument.getString("_id");

			List<ReviewXml> reviews = new ArrayList<>();

			List<Document> reviewDocuments = itemDocument.get("review", List.class);

			for (Document reviewDocument : reviewDocuments) {

				String rating = reviewDocument.getString("rating");
				String comment = reviewDocument.getString("comment");

				ReviewXml reviewXml = new ReviewXml(rating, comment);
				reviews.add(reviewXml);
			}

			ItemXml itemXml = new ItemXml(itemId, reviews);
			items.add(itemXml);
		}

		ReviewsXml reviewsXml = new ReviewsXml(items);
		exchange.getIn().setBody(reviewsXml);

	}

	public void itemDetailsByIdResponse(Exchange exchange) {

		String categoryName = exchange.getProperty("categoryName", String.class);

		Document itemDocument = exchange.getIn().getBody(Document.class);

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
}
