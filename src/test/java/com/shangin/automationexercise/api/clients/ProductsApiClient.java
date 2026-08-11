package com.shangin.automationexercise.api.clients;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

import com.shangin.automationexercise.config.ConfigReader;

public class ProductsApiClient {
    
    private static final String PRODUCT_LIST_ENDPOINT = "/productsList";
    private static final String SEARCH_PRODUCTS_ENDPOINT = "/searchProduct";

    public Response getProductsList() {
        return given()
                .baseUri(ConfigReader.getApiBaseUrl())
                .when()
                .get(PRODUCT_LIST_ENDPOINT);
    }

    public Response postProductsList() {
        return given()
                .baseUri(ConfigReader.getApiBaseUrl())
                .when()
                .post(PRODUCT_LIST_ENDPOINT);
    }
    
    public Response postSearchProducts(String query) {
        
        return given()
                .baseUri(ConfigReader.getApiBaseUrl())
                .contentType("application/x-www-form-urlencoded")
                .formParam("search_product", query)
                .when()
                .post(SEARCH_PRODUCTS_ENDPOINT);
    }
    
    public Response postSearchProductsWithoutSearchProductParameter() {
        
        return given()
                .baseUri(ConfigReader.getApiBaseUrl())
                .contentType("application/x-www-form-urlencoded")
                .when()
                .post(SEARCH_PRODUCTS_ENDPOINT);
    }
}
