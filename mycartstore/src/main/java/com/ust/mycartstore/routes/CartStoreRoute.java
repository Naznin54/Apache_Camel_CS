package com.ust.mycartstore.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import com.ust.mycartstore.constants.ApplicationConstants;
import com.ust.mycartstore.model.Item;
import com.ust.mycartstore.processor.ItemDetailProcessor;

@Component
public class CartStoreRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

	onException(HttpOperationFailedException.class).onWhen(simple("${exception.statusCode} == 404")).handled(true)
		 .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404)).setHeader("Content-Type", constant("application/json"))
		 .setBody(constant("{\"error\":\"{{error.itemNotFound}}\"}"));

	onException(Throwable.class).handled(true).setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
		  .setHeader("Content-Type", constant("application/json"))
		  .setBody(constant("{\"error\":\"{{error.internalError}}\"}"));

	rest().get("/item/{item_id}").to(ApplicationConstants.GET_ITEM_DETAILS);
	
	from(ApplicationConstants.GET_ITEM_DETAILS).routeId("direct.getItems")
		 .toD("{{camel.itemResponseById}}/${header.item_id}?bridgeEndpoint=true")
		 .unmarshal().json(JsonLibrary.Jackson, Item.class)
		 .process(new ItemDetailProcessor()).marshal().json(JsonLibrary.Jackson);

	}

}
