package com.shangin.automationexercise.api.specs;

import com.shangin.automationexercise.config.ConfigReader;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;

public class ApiSpecifications {

    private static final RequestSpecification REQUEST_SPEC = 
            new RequestSpecBuilder()
                .setBaseUri(ConfigReader.getApiBaseUrl())
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new AllureRestAssured())
                .build();

    private ApiSpecifications() {
    }

    public static RequestSpecification defaultRequest() {
        return given().spec(REQUEST_SPEC);
    }
}
