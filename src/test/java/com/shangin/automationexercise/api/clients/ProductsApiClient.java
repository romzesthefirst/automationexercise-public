package com.shangin.automationexercise.api.clients;

import io.qameta.allure.Step;

import io.restassured.response.Response;

import com.shangin.automationexercise.api.specs.ApiSpecifications;

public class ProductsApiClient {
    
    private static final String PRODUCT_LIST_ENDPOINT = "/productsList";
    private static final String SEARCH_PRODUCTS_ENDPOINT = "/searchProduct";

    @Step("Browse the product catalog")
    public Response getProductsList() {
        return ApiSpecifications.defaultRequest()
                .when()
                .get(PRODUCT_LIST_ENDPOINT);
    }

    @Step("Check that the catalog rejects unsupported POST requests")
    public Response postProductsList() {
        return ApiSpecifications.defaultRequest()
                .when()
                .post(PRODUCT_LIST_ENDPOINT);
    }
    
    @Step("Search the catalog for {query}")
    public Response postSearchProducts(String query) {
        return ApiSpecifications.formRequest()
                .formParam("search_product", query)
                .when()
                .post(SEARCH_PRODUCTS_ENDPOINT);
    }
    
    @Step("Search without a query to verify validation")
    public Response postSearchProductsWithoutSearchProductParameter() {
        return ApiSpecifications.formRequest()
                .when()
                .post(SEARCH_PRODUCTS_ENDPOINT);
    }
}
