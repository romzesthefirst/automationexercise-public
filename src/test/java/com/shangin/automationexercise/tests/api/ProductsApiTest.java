package com.shangin.automationexercise.tests.api;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.shangin.automationexercise.api.clients.ProductsApiClient;
import com.shangin.automationexercise.api.models.ProductDto;
import com.shangin.automationexercise.api.support.ApiResponseParser;
import com.shangin.automationexercise.assertions.ProductApiAssertions;
import com.shangin.automationexercise.constants.ApiMessages;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import tools.jackson.databind.JsonNode;

@Test(groups = "api")
public class ProductsApiTest {

    private final ProductsApiClient productsApiClient = new ProductsApiClient();
    private final ProductsApiClient searchProductsApiClient = new ProductsApiClient();

    @Test(groups = "smoke") @Description("API 1: Get All Products List")
    public void shouldReturnProductsList() {
        // API URL: https://automationexercise.com/api/productsList
        // Request Method: GET
        Response response = productsApiClient.getProductsList();

        JsonNode body = ApiResponseParser.extractJson(response);

        // Response Code: 200
        Assert.assertEquals(response.statusCode(), 200, "Unexpected HTTP status code");

        Assert.assertEquals(
                body.get("responseCode").asInt(),
                200,
                "Unexpected responseCode in response body");

        // Response JSON: All products list
        List<ProductDto> products
                = ApiResponseParser.extractList(response, "products", ProductDto.class);

        ProductApiAssertions.assertValidProducts(products);
    }

    @Test @Description("API 2: POST To All Products List")
    public void shouldRejectPostRequestToProductsList() {

        // API URL: https://automationexercise.com/api/productsList
        // Request Method: POST
        Response response = productsApiClient.postProductsList();

        JsonNode body = ApiResponseParser.extractJson(response);
        Assert.assertEquals(response.statusCode(), 200, "Unexpected HTTP status code");

        // Response Code: 405
        Assert.assertEquals(body.get("responseCode").asInt(), 405, "Unexpected responseCode");

        // Response Message: This request method is not supported.
        Assert.assertEquals(
                body.get("message").asString(),
                ApiMessages.REQUEST_METHOD_IS_NOT_SUPPORTED,
                "Unexpected response message");
    }

    @Test @Description("API 5: POST To Search Product")
    public void shouldSearchProducts() {
        // API URL: https://automationexercise.com/api/searchProduct
        // Request Method: POST
        // Request Parameter: search_product (For example: top, tshirt, jean)
        String query = "tshirt";

        Response response = searchProductsApiClient.postSearchProducts(query);

        JsonNode body = ApiResponseParser.extractJson(response);

        // Response Code: 200
        Assert.assertEquals(response.statusCode(), 200, "Unexpected HTTP status code");
        Assert.assertEquals(
                body.get("responseCode").asInt(),
                200,
                "Unexpected responseCode in response body");

        // Response JSON: Searched products list
        List<ProductDto> products
                = ApiResponseParser.extractList(response, "products", ProductDto.class);

        ProductApiAssertions.assertValidSearchProducts(products, query);
    }

    @Test @Description("API 6: POST To Search Product without search_product parameter")
    public void shouldRejectSearchWithoutSearchProductParameter() {
        // API URL: https://automationexercise.com/api/searchProduct
        // Request Method: POST
        Response response
                = searchProductsApiClient.postSearchProductsWithoutSearchProductParameter();

        JsonNode body = ApiResponseParser.extractJson(response);

        // Response Code: 400
        Assert.assertEquals(response.statusCode(), 200, "Unexpected HTTP status code");
        Assert.assertEquals(
                body.get("responseCode").asInt(),
                400,
                "Unexpected responseCode in response body");

        // Response Message: Bad request, search_product parameter is missing in POST
        // request.
        Assert.assertEquals(
                body.get("message").asString(),
                ApiMessages.SEARCH_PRODUCT_PARAMETER_IS_MISSING,
                "Unexpected error message");
    }

}