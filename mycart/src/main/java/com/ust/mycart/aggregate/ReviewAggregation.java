package com.ust.mycart.aggregate;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

import com.ust.mycart.xml.model.ReviewsXml;

public class ReviewAggregation implements AggregationStrategy {

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

		if (oldExchange == null) {
			return newExchange;
		}

		ReviewsXml oldReviewsXml = oldExchange.getIn().getBody(ReviewsXml.class);
		ReviewsXml newReviewsXml = newExchange.getIn().getBody(ReviewsXml.class);

		oldReviewsXml.getItems().addAll(newReviewsXml.getItems());

		oldExchange.getIn().setBody(oldReviewsXml);
		return oldExchange;
	}
}
