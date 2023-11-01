package com.ust.mycart.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.ust.mycart.aggregate.ItemInventoryAggregation;
import com.ust.mycart.aggregate.ItemResponseAggregation;
import com.ust.mycart.aggregate.ReviewAggregation;
import com.ust.mycart.bean.SftpBean;
import com.ust.mycart.constants.ApplicationConstants;

@Component
public class ItemRoutes extends RouteBuilder {

	@Autowired
	private SftpBean sftpProductBean;

	@Value("${camel.sftp.maximumMessageCount}")
	private int maximumMessageCount;

	@Value("${camel.sftp.timePeriod}")
	private int timePeriod;

	@Value("${sftp.password}")
	private String password;

	@Value("${camel.mongodb.database}")
	private String database;

	@Value("${camel.mongodb.item.collection}")
	private String item;

	@Value("${camel.mongodb.category.collection}")
	private String category;

	@Value("${camel.mongodb.ControlRef.collection}")
	private String controlRef;

	@Override
	public void configure() throws Exception {

		onException(Throwable.class).handled(true).setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
		   .setHeader("Content-Type", constant("application/json")).log(LoggingLevel.ERROR, "${exception.message}");

		/**
		 * route to send files to FTP
		 */

		from(ApplicationConstants.FTP_UPLOAD).routeId("direct.uploadFtp")
				.to("ftp://{{sftp.link}}?password=" + password + "&fileName=${exchangeProperty.CamelFileName}");

		/**
		 * Route to fetch the LastProcessTs from controlRef
		 */

		from(ApplicationConstants.LAST_PROCESSTs).routeId("direct.getLastProcessTs").setBody(constant("conrolrefid"))
				.to("mongodb:myDB?database=" + database + "&collection=" + controlRef + "&operation=findById")
				.setProperty(ApplicationConstants.LASTPROCESSTs, simple("${body[lastProcessTs]}"));

		/**
		 * Route to find all items where lastUpdateDate is greater than LastProcessTs
		 */

		from(ApplicationConstants.LASTPROCESSTs_FIND_ITEMS).routeId("direct.lastProcessTsFindItems")
				.to(ApplicationConstants.LAST_PROCESSTs).setHeader(ApplicationConstants.MONGO_DB_CRITERIA,
						simple("{ \"lastUpdateDate\": { \"$gt\": \"${exchangeProperty.lastProcessTs}\" } }"))
				.to(ApplicationConstants.FIND_ALL_ITEMS);

		/**
		 * Multicast to three routes via cron
		 */
		from("cron:myScheduler?schedule=0 */10 * * * ?")
		        .to(ApplicationConstants.LASTPROCESSTs_FIND_ITEMS)
		        .multicast().parallelProcessing().to(ApplicationConstants.ITEM_TREND_ANALYZER,
						   ApplicationConstants.ITEM_REVIEW_AGGREGATOR, ApplicationConstants.STORE_FRONT_APP)
				.to(ApplicationConstants.UPDATE_CONTROLREF).end();

		/**
		 * Route to update lastProcessDate in controlRef collection
		 */
		from(ApplicationConstants.UPDATE_CONTROLREF).routeId("direct.updateControlRef")
				.setProperty(ApplicationConstants.LASTPROCESSTs, simple("${date:now:yyyy-MM-dd HH:mm:ss}"))
				.setHeader(ApplicationConstants.MONGO_DB_CRITERIA).simple("{\"_id\": \"conrolrefid\"}")
				.setBody(simple("{\"$set\": {\"lastProcessTs\": \"${exchangeProperty.lastProcessTs}\"}}"))
				.to("mongodb:myDB?database=" + database + "&collection=" + controlRef + "&operation=update")
				.log(LoggingLevel.INFO, "LastProcessTs updated");

		/**
		 * Route to find categoryName from categoryId
		 */
		from(ApplicationConstants.FIND_CATEGORY_NAME).routeId("direct.findCategoryName")
				.setProperty(ApplicationConstants.CATEGORY_ID, simple("${body[categoryId]}"))
				.setHeader(ApplicationConstants.MONGO_DB_CRITERIA,
						              simple("{ \"_id\": '${exchangeProperty.categoryId}'}"))
				.to(ApplicationConstants.FIND_CATEGORY_ID)
				.setProperty(ApplicationConstants.CATEGORY_NAME, simple("${body[0][categoryName]}"));

		/**
		 * Req 3 : itemTrendAnalyzer.xml
		 */

		from(ApplicationConstants.ITEM_TREND_ANALYZER).routeId("direct.itemTrendAnalyzer").split().body()
				.setProperty(ApplicationConstants.MESSAGE_BODY, body()).to(ApplicationConstants.FIND_CATEGORY_NAME)
				.setBody(simple("${exchangeProperty.messagebody}")).bean(sftpProductBean, "itemInventoryResponse")
				.aggregate(constant(true), new ItemInventoryAggregation()).completionTimeout(2000)
				.setProperty(Exchange.FILE_NAME, constant("ItemTrendAnalyzer.xml"))
				.log(LoggingLevel.INFO, "send Items for ItemTrendAnalyzer").throttle(maximumMessageCount)
				.timePeriodMillis(timePeriod).to(ApplicationConstants.FTP_UPLOAD);

		/**
		 * Req 3 : reviewDump.xml
		 */

		from(ApplicationConstants.ITEM_REVIEW_AGGREGATOR).routeId("direct.itemReviewAggregator").split().body()
				.bean(sftpProductBean, "reviewDumpResponse").aggregate(constant(true), new ReviewAggregation())
				.completionTimeout(2000).setProperty(Exchange.FILE_NAME, constant("ReviewDump.xml"))
				.log(LoggingLevel.INFO, "send items for reviewAggregator").throttle(maximumMessageCount)
				.timePeriodMillis(timePeriod).to(ApplicationConstants.FTP_UPLOAD);

		/**
		 * Req 3 : storeFrontApp.json
		 */

		from(ApplicationConstants.STORE_FRONT_APP).routeId("direct.storeFront").split().body()
				.to(ApplicationConstants.ITEM_RESPONSE).bean(SftpBean.class, "itemDetailsByIdResponse")
				.aggregate(constant(true), new ItemResponseAggregation()).completionTimeout(2000).marshal()
				.json(JsonLibrary.Jackson, true).setProperty(Exchange.FILE_NAME, constant("StoreFront.json"))
				.log(LoggingLevel.INFO, "send Items for storeFront ").throttle(maximumMessageCount)
				.timePeriodMillis(timePeriod).to(ApplicationConstants.FTP_UPLOAD);

	}
}