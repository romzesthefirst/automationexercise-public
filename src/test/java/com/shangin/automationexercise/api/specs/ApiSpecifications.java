package com.shangin.automationexercise.api.specs;

import com.shangin.automationexercise.config.ConfigReader;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class ApiSpecifications {

    private ApiSpecifications() {
    }

    public static RequestSpecification defaultRequest() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigReader.getApiBaseUrl())
                .build();
    }
}
