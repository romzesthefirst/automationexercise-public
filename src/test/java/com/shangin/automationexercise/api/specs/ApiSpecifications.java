package com.shangin.automationexercise.api.specs;

import com.shangin.automationexercise.config.ConfigReader;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;

public class ApiSpecifications {

    private ApiSpecifications() {
    }

    public static RequestSpecification defaultRequest() {
        ConfigReader.validate();
        return withDiagnostics(new RequestSpecBuilder().setBaseUri(ConfigReader.getApiBaseUrl()).build());
    }
    
    public static RequestSpecification formRequest() {
        ConfigReader.validate();
        return withDiagnostics(new RequestSpecBuilder().setBaseUri(ConfigReader.getApiBaseUrl())
                .setContentType("application/x-www-form-urlencoded").build());
    }
    private static RequestSpecification withDiagnostics(RequestSpecification specification) {
        // Bind logging to the current output stream for each request, rather than a shared filter.
        return given().spec(specification)
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .filter(new AllureRestAssured());
    }
}
