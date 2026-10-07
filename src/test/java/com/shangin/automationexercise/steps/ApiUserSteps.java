package com.shangin.automationexercise.steps;

import com.shangin.automationexercise.api.clients.AccountApiClient;
import com.shangin.automationexercise.api.support.ApiResponseParser;
import com.shangin.automationexercise.api.support.OwnedAccounts;
import com.shangin.automationexercise.model.User;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.testng.Assert;
import tools.jackson.databind.JsonNode;

public class ApiUserSteps {

    private final AccountApiClient accountApiClient;

    private final OwnedAccounts ownedAccounts;

    public ApiUserSteps(AccountApiClient accountApiClient, OwnedAccounts ownedAccounts) {
        this.accountApiClient = accountApiClient;
        this.ownedAccounts = ownedAccounts;
    }

    @Step("Create an owned test account and verify registration")
    public User createUser() {

        User user = ownedAccounts.newUser();

        Response response = accountApiClient.createAccount(user);
        JsonNode body = ApiResponseParser.extractJson(response);

        Assert.assertEquals(
                response.statusCode(), 200, "Failed to create test user: unexpected HTTP status");

        Assert.assertEquals(
                body.get("responseCode").asInt(),
                201,
                "Failed to create test user: unexpected responseCode");

        return user;
    }
}
