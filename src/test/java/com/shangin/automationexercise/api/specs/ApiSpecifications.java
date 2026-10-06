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
                .build();

    private static final RequestSpecification FORM_REQUEST_SPEC =
            new RequestSpecBuilder()
                    .addRequestSpecification(REQUEST_SPEC)
                    .setContentType("application/x-www-form-urlencoded")
                    .build();
    
    private ApiSpecifications() {
    }

    public static RequestSpecification defaultRequest() {
        return withDiagnostics(REQUEST_SPEC);
    }
    
    public static RequestSpecification formRequest() {
        return withDiagnostics(FORM_REQUEST_SPEC);
    }
    private static RequestSpecification withDiagnostics(RequestSpecification specification) {
        // Bind logging to the current output stream for each request, rather than a shared filter.
        return given().spec(specification)
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .filter(new AllureRestAssured());
    }
}
