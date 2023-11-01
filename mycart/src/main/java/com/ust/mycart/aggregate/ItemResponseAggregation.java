package com.ust.mycart.aggregate;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

import com.ust.mycart.response.model.ItemResponse;

public class ItemResponseAggregation implements AggregationStrategy {

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

		ItemResponse newItemResponse = newExchange.getIn().getBody(ItemResponse.class);

		List<ItemResponse> aggregatedResponses = (oldExchange == null) ? new ArrayList<>()
				: oldExchange.getIn().getBody(List.class);

		if (newItemResponse != null) {
			aggregatedResponses.add(newItemResponse);
		}

		newExchange.getIn().setBody(aggregatedResponses);

		return newExchange;

	}

}
