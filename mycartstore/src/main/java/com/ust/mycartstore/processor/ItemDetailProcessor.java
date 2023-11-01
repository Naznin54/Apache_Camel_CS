package com.ust.mycartstore.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.ust.mycartstore.model.Item;

public class ItemDetailProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {

		Item responseBody = exchange.getIn().getBody(Item.class);

		Item itemDetails = new Item();

		itemDetails.setId(responseBody.getId());
		itemDetails.setItemName(responseBody.getItemName());
		itemDetails.setCategoryName(responseBody.getCategoryName());

		itemDetails.setItemPrice(responseBody.getItemPrice());
		itemDetails.setStockDetails(responseBody.getStockDetails());
		itemDetails.setSpecialProduct(responseBody.isSpecialProduct());

		exchange.getIn().setBody(itemDetails);
	}

}
