package com.ust.updatesubscriberservice.processor;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.bson.Document;

public class UpdateItemProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		
		 Document itemDocument = exchange.getIn().getBody(Document.class);

		   int soldOut = exchange.getProperty("soldOut", Integer.class);
		   int damaged = exchange.getProperty("damaged", Integer.class);
		   	    
		   Document stockDetails = itemDocument.get("stockDetails", Document.class);
	 
		    // Retrieve the current availableStock value
			int currentAvailableStock = stockDetails.getInteger("availableStock", 0);
			
			int newAvailableStock = currentAvailableStock - soldOut - damaged;
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    String lastupdatedate = dateFormat.format(new Date());
			itemDocument.put("lastUpdateDate", lastupdatedate);

			// Update the availableStock field in the stockDetails object
			stockDetails.put("availableStock", newAvailableStock);
			
	        // Set the updated stockDetails object back into the item document
			itemDocument.put("stockDetails", stockDetails);
			exchange.getIn().setBody(itemDocument);
		
		
	
	}

}
