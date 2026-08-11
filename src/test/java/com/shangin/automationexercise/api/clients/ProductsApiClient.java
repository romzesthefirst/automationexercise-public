package com.shangin.automationexercise.api.clients;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

import com.shangin.automationexercise.config.ConfigReader;

public class ProductsApiClient {

    public Response getProducts() {
        return given().baseUri(ConfigReader.getApiBaseUrl()).when().get("/productsList");
    }
}
