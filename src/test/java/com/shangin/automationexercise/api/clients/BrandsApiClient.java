package com.shangin.automationexercise.api.clients;

import com.shangin.automationexercise.api.specs.ApiSpecifications;

import io.restassured.response.Response;

public class BrandsApiClient {

    private static final String BRANDS_ENDPOINT = "/brandsList";
    
    public Response getBrandsList() {
        return ApiSpecifications.defaultRequest()
                .when()
                .get(BRANDS_ENDPOINT);
    }

    public Response putBrandsList() {
        return ApiSpecifications.defaultRequest()
                .when()
                .put(BRANDS_ENDPOINT);
    }
}
