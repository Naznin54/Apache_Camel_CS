package com.ust.mycart.aggregate;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

import com.ust.mycart.xml.model.CategoryXml;
import com.ust.mycart.xml.model.InventoryXml;

public class ItemInventoryAggregation implements AggregationStrategy {

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
				
		
		        if (oldExchange == null) {
		            return newExchange;
		        }

		        InventoryXml oldInventoryXml = oldExchange.getIn().getBody(InventoryXml.class);
		        InventoryXml newInventoryXml = newExchange.getIn().getBody(InventoryXml.class);

		        // Combine categories from old and new inventory
		        for (CategoryXml newCategory : newInventoryXml.getCategoriesList()) {
		            boolean categoryExists = false;

		            // Check if the category already exists in the old inventory
		            for (CategoryXml oldCategory : oldInventoryXml.getCategoriesList()) {
		                if (newCategory.getCategoryId().equals(oldCategory.getCategoryId())) {
		                    categoryExists = true;

		                    // Add items from the new category to the existing category
		                    oldCategory.getItemDetailList().addAll(newCategory.getItemDetailList());
		                    break;
		                }
		            }

		            // If the category doesn't exist in the old inventory, add it
		            if (!categoryExists) {
		                oldInventoryXml.getCategoriesList().add(newCategory);
		            }
		        }

		        // Set the combined inventory as the body of the old exchange
		        oldExchange.getIn().setBody(oldInventoryXml);
		        return oldExchange;
		    }
		}


