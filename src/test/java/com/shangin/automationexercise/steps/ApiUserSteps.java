package com.shangin.automationexercise.steps;

import org.testng.Assert;

import com.shangin.automationexercise.api.clients.AccountApiClient;
import com.shangin.automationexercise.api.support.ApiResponseParser;
import com.shangin.automationexercise.factories.UserFactory;
import com.shangin.automationexercise.model.User;

import io.restassured.response.Response;
import tools.jackson.databind.JsonNode;

public class ApiUserSteps {

    private final AccountApiClient accountApiClient;

    public ApiUserSteps(AccountApiClient accountApiClient) {
        this.accountApiClient = accountApiClient;
    }

    public User createUser() {
        
        User user = UserFactory.randomUser();

        Response response = accountApiClient.createAccount(user);
        JsonNode body = ApiResponseParser.extractJson(response);

        Assert.assertEquals(
                response.statusCode(),
                200,
                "Failed to create test user: unexpected HTTP status");

        Assert.assertEquals(
                body.get("responseCode").asInt(),
                201,
                "Failed to create test user: unexpected responseCode");

        return user;
    }
}
