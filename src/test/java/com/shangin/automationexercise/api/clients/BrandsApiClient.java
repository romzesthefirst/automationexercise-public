package com.shangin.automationexercise.api.clients;

import com.shangin.automationexercise.api.specs.ApiSpecifications;
import io.qameta.allure.Step;
import io.restassured.response.Response;

public class BrandsApiClient {

    private static final String BRANDS_ENDPOINT = "/brandsList";

    @Step("Browse available brands")
    public Response getBrandsList() {
        return ApiSpecifications.defaultRequest().when().get(BRANDS_ENDPOINT);
    }

    @Step("Check that brands reject unsupported PUT requests")
    public Response putBrandsList() {
        return ApiSpecifications.defaultRequest().when().put(BRANDS_ENDPOINT);
    }
}
