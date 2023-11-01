package com.ust.updaterestservice.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.ust.updaterestservice.constants.ApplicationConstants;

@Component
public class ProducerRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		onException(Throwable.class).handled(true).setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
				.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
				.setBody(constant("{\"result\":\"{{error.internalServerError}}\"}"));

		rest().put("/updateItemDetails").consumes("application/json").to(ApplicationConstants.UPDATE_ITEM);

		from(ApplicationConstants.UPDATE_ITEM).routeId("direct.updateItemProducer")
		        .to("activemq:queue:updateItemQueue")
				.setBody(constant("{\"message\":\"{{activeMq.producerResponse}}\"}"));

	}

}
