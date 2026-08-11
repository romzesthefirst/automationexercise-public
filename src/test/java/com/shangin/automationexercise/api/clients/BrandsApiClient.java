package com.shangin.automationexercise.api.clients;

import static io.restassured.RestAssured.given;

import com.shangin.automationexercise.config.ConfigReader;

import io.restassured.response.Response;

public class BrandsApiClient {

    private static final String BRANDS_ENDPOINT = "/brandsList";
    
    public Response getBrandsList() {
        return given().baseUri(ConfigReader.getApiBaseUrl()).when().get(BRANDS_ENDPOINT);
    }

    public Response putBrandsList() {
        return given().baseUri(ConfigReader.getApiBaseUrl()).when().put(BRANDS_ENDPOINT);
    }
}
