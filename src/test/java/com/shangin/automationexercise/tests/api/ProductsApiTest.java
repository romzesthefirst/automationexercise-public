package com.shangin.automationexercise.tests.api;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.shangin.automationexercise.api.clients.ProductsApiClient;
import com.shangin.automationexercise.api.models.ProductDto;
import com.shangin.automationexercise.api.support.ApiResponseParser;
import com.shangin.automationexercise.assertions.ProductApiAssertions;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import tools.jackson.databind.JsonNode;

public class ProductsApiTest {

    private final ProductsApiClient productsApiClient = new ProductsApiClient();

    @Test @Description("API 1: Get All Products List")
    public void shouldReturnProductsList() {
        // API URL: https://automationexercise.com/api/productsList
        // Request Method: GET
        Response response = productsApiClient.getProducts();

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
}