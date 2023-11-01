package com.ust.mycart.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mongodb.CamelMongoDbException;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.ust.mycart.bean.ItemBean;
import com.ust.mycart.constants.ApplicationConstants;
import com.ust.mycart.exception.CategoryNotFoundException;
import com.ust.mycart.exception.ItemNotFoundException;
import com.ust.mycart.exception.ItemExistsException;
import com.ust.mycart.exception.ItemPriceBadRequestException;
import com.ust.mycart.model.Item;

@Component
public class MyCartRoutes extends RouteBuilder {

	@Autowired
	private ItemBean productBean;

	@Value("${camel.maximumRedeliveries}")
	private int maximumRedeliveries;

	@Value("${camel.redeliveryDelay}")
	private int redeliveryDelay;

	@Value("${camel.backOffMultiplier}")
	private int backOffMultiplier;

	@Value("${camel.mongodb.database}")
	private String database;

	@Value("${camel.mongodb.item.collection}")
	private String item;

	@Value("${camel.mongodb.category.collection}")
	private String category;

	@Override
	public void configure() throws Exception {

		onException(ItemNotFoundException.class).handled(true).setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
				.setHeader("Content-Type", constant("application/json"))
				.setBody(constant("{\"error\":\"{{error.itemNotFound}}\"}")).log(LoggingLevel.ERROR, "Item not Found");

		onException(CategoryNotFoundException.class).handled(true).setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
				.setHeader("Content-Type", constant("application/json"))
				.setBody(constant("{\"error\":\"{{error.categoryNotFound}}\"}"))
				.log(LoggingLevel.ERROR, "Category not Found");

		onException(ItemExistsException.class).handled(true).setHeader(Exchange.HTTP_RESPONSE_CODE, constant(409))
				.setHeader("Content-Type", constant("application/json"))
				.setBody(constant("{\"error\":\"{{error.itemExists}}\"}"))
				.log(LoggingLevel.ERROR, "Item already Exists");

		onException(ItemPriceBadRequestException.class).handled(true)
		        .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
				.setHeader("Content-Type", constant("application/json"))
				.setBody(constant("{\"error\":\"{{error.ItemPrice}}\"}"))
				.log(LoggingLevel.ERROR, "Base price and selling price must be greater than zero");

		onException(CamelMongoDbException.class).maximumRedeliveries(maximumRedeliveries)
				.redeliveryDelay(redeliveryDelay).backOffMultiplier(backOffMultiplier).handled(true);

		onException(Throwable.class).handled(true).setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
				.setHeader("Content-Type", constant("application/json"))
				.setBody(constant("{\"error\":\"{{error.internalError}}\"}"))
				.log(LoggingLevel.ERROR, "${exception.message}");

		/**
		 * API to access details by item id
		 */
		rest("/getItemDetails").get("/{itemId}").to(ApplicationConstants.GET_ITEM_DETAILS);

		/**
		 * API to access details by category id
		 */
		rest("/mycart").get("items/{category_id}").to(ApplicationConstants.GET_ITEMBY_CATEGORYNAMEDEPT);

		/**
		 * API to add an item
		 */
		rest().post("/addNewItem").consumes("application/json").type(Item.class).to(ApplicationConstants.ADD_NEW_ITEM);

		/**
		 * API to update an item
		 */
		rest().put("/updateItem").consumes("application/json").type(Item.class).to(ApplicationConstants.UPDATE_ITEM);

		from(ApplicationConstants.INSERT_ITEM).routeId("direct.addItem")
				.to("mongodb:myDB?database=" + database + "&collection=" + item + "&operation=insert");

		from(ApplicationConstants.FIND_ITEMS_ID).routeId("direct.findItemByIdRoute")
				.to("mongodb:myDB?database=" + database + "&collection=" + item + "&operation=findById");

		from(ApplicationConstants.FIND_CATEGORY_ID).routeId("direct.findCategoryByIdRoute")
				.to("mongodb:myDB?database=" + database + "&collection=" + category + "&operation=findById");

		from(ApplicationConstants.FIND_ALL_ITEMS).routeId("direct.findAllItems")
				.to("mongodb:myDB?database=" + database + "&collection=" + item + "&operation=findAll");

		from(ApplicationConstants.FIND_ALL_CATEGORY).routeId("direct.findAllCategories")
				.to("mongodb:myDB?database=" + database + "&collection=" + category + "&operation=findAll");

		from(ApplicationConstants.SAVE_ITEM).routeId("direct.saveItem")
				.to("mongodb:myDB?database=" + database + "&collection=" + item + "&operation=save");

		/**
		 * Route to get Item response by categoryId
		 */

		from(ApplicationConstants.ITEM_RESPONSE).routeId("direct.itemResponse")
				.setProperty(ApplicationConstants.MESSAGE_BODY, body())
				.setProperty(ApplicationConstants.CATEGORY_ID, simple("${body[categoryId]}"))
				.setBody(simple("${exchangeProperty.categoryId}")).to(ApplicationConstants.FIND_CATEGORY_ID)
				.setProperty(ApplicationConstants.CATEGORY_NAME, simple("${body[categoryName]}"))
				.setBody(simple("${exchangeProperty.messagebody}"));

		/**
		 * Req 1 : API Route to access Item details by itemId
		 */

		from(ApplicationConstants.GET_ITEM_DETAILS).routeId("direct.getItemDetails")
				.setBody(header(ApplicationConstants.ITEMID)).to(ApplicationConstants.FIND_ITEMS_ID).choice()
				.when(body().isNull()).throwException(new ItemNotFoundException()).otherwise()
				.to(ApplicationConstants.ITEM_RESPONSE).bean(productBean, "ItemDetailsResponse").marshal().json()
				.log(LoggingLevel.INFO, "Received items").endChoice();

		/**
		 * Req 1 : API Route to access details by category id and accepts includeSpecial
		 */

		from(ApplicationConstants.GET_ITEMBY_CATEGORYNAMEDEPT).routeId("direct.getItemByCategoryNameDept")
				.setBody(header(ApplicationConstants.CATEGORYID)).to(ApplicationConstants.FIND_CATEGORY_ID)
				.setProperty(ApplicationConstants.CATEGORY_ID, simple("${body[_id]}"))
				.setProperty(ApplicationConstants.CATEGORY_NAME, simple("${body[categoryName]}"))
				.setProperty(ApplicationConstants.CATEGORY_DEP, simple("${body[categoryDep]}"))
				.bean(productBean, "categoryNameDeptCriteria").to(ApplicationConstants.FIND_ALL_ITEMS)

				.choice().when(simple("${body.isEmpty()}")).throwException(new CategoryNotFoundException()).otherwise()
				.bean(productBean, "CategoryNameDeptResponse").marshal().json()
				.log(LoggingLevel.INFO, "Received items by categoryNameDepartment").end();

		/**
		 * Req 1 : API Route to add item
		 */
		from(ApplicationConstants.ADD_NEW_ITEM).routeId("direct.addNewItem")
				.setProperty(ApplicationConstants.ITEM_ID, simple("${body[_id]}"))
				.setProperty(ApplicationConstants.CATEGORY_ID, simple("${body[categoryId]}")).unmarshal()
				.json(JsonLibrary.Jackson, Item.class).setProperty(ApplicationConstants.MESSAGE_BODY, simple("${body}"))

				.choice().when(simple("${body.itemPrice.basePrice} <= 0 || ${body.itemPrice.sellingPrice} <= 0"))
				.throwException(new ItemPriceBadRequestException()).otherwise()
				.setBody(simple("${exchangeProperty.itemId}")).to(ApplicationConstants.FIND_ITEMS_ID)

				.choice().when(body().isNotNull()).throwException(new ItemExistsException()).otherwise()
				.setBody(simple("${exchangeProperty.categoryId}")).to(ApplicationConstants.FIND_CATEGORY_ID)

				.choice().when(body().isNull()).throwException(new CategoryNotFoundException()).otherwise()
				.setBody(simple("${exchangeProperty.messagebody}")).bean(productBean, "addNewItem")
				.to(ApplicationConstants.INSERT_ITEM).marshal().json()
				.setBody(constant("{\"message\":\"New Item added successfully\"}")).endChoice().end()
				.log(LoggingLevel.INFO, "Added Item").end();

		/**
		 * Req 1 : API Route to update an item details
		 */
		from(ApplicationConstants.UPDATE_ITEM).routeId("direct.updateItem").split(simple("${body[items]}"))
				.setHeader(ApplicationConstants.ITEMID, simple("${body[_id]}"))
				.setProperty("soldOut", simple("${body[stockDetails][soldOut]}"))
				.setProperty("damaged", simple("${body[stockDetails][damaged]}")).setBody(simple("${header.itemId}"))
				.to(ApplicationConstants.FIND_ITEMS_ID)

				.choice().when(body().isNull()).throwException(new ItemNotFoundException()).otherwise()
				.bean(productBean, "updateItem").to(ApplicationConstants.SAVE_ITEM).marshal().json().end().end()
				.log(LoggingLevel.INFO, "Updated Items").setHeader("CamelHttpResponseCode", constant(201));

	}
}
