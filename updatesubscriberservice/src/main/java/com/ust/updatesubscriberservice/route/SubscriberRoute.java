package com.ust.updatesubscriberservice.route;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ust.updatesubscriberservice.constants.ApplicationConstants;
import com.ust.updatesubscriberservice.exception.ItemNotFoundException;
import com.ust.updatesubscriberservice.processor.UpdateItemProcessor;

@Component
public class SubscriberRoute extends RouteBuilder {

	@Value("${camel.mongodb.database}")
	private String database;

	@Value("${camel.mongodb.item.collection}")
	private String item;

	@Value("${camel.mongodb.category.collection}")
	private String category;

	@Override
	public void configure() throws Exception {

		onException(ItemNotFoundException.class).handled(true).setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
				.setHeader("Content-Type", constant("application/json")).log("${exception.message}");

		from(ApplicationConstants.FIND_ITEM_ID).routeId("direct.findItemById")
				.to("mongodb:myDB?database=" + database + "&collection=" + item + "&operation=findById");

		from(ApplicationConstants.SAVE_ITEM).routeId("direct.saveItem")
				.to("mongodb:myDB?database=" + database + "&collection=" + item + "&operation=save");

		from("activemq:queue:updateItemQueue?concurrentConsumers=4").routeId("updateItemSubscriberRoute")
				.split(simple("${body[items]}")).parallelProcessing().setHeader("itemId", simple("${body[_id]}"))
				.setProperty("soldOut", simple("${body[stockDetails][soldOut]}"))
				.setProperty("damaged", simple("${body[stockDetails][damaged]}")).setBody(simple("${header.itemId}"))
				.to(ApplicationConstants.FIND_ITEM_ID)

				.choice().when(body().isNull())
				.throwException(new ItemNotFoundException("Item Id not found for update.")).otherwise()
				.process(new UpdateItemProcessor()).to(ApplicationConstants.SAVE_ITEM).marshal().json().end()
				.log(LoggingLevel.INFO, "Updated Item").end().setHeader("CamelHttpResponseCode", constant(201));

	}

}
