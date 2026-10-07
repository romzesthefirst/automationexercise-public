package com.shangin.automationexercise.tests.api;

import com.shangin.automationexercise.api.models.UserDetailsResponseDto;
import com.shangin.automationexercise.api.support.ApiResponseParser;
import com.shangin.automationexercise.assertions.UserApiAssertions;
import com.shangin.automationexercise.base.AccountTestBase;
import com.shangin.automationexercise.constants.ApiMessages;
import com.shangin.automationexercise.factories.UserFactory;
import com.shangin.automationexercise.model.User;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import tools.jackson.databind.JsonNode;

@Test(groups = "api")
@Epic("Automation Exercise")
@Feature("Account")
public class AccountApiTest extends AccountTestBase {

    @Test
    @Description("API 11: POST To Create/Register User Account")
    public void shouldCreateNewUserAccount() {

        User user = accounts.newUser();

        // API URL: https://automationexercise.com/api/createAccount
        // Request Method: POST
        // Request Parameters: name, email, password, title (for example: Mr, Mrs,
        // Miss), birth_date, birth_month, birth_year, firstname, lastname, company,
        // address1, address2, country, zipcode, state, city, mobile_number
        Response createAccountresponse = accountApiClient.createAccount(user);

        Assert.assertEquals(createAccountresponse.statusCode(), 200, "Unexpected HTTP status code");

        JsonNode body = ApiResponseParser.extractJson(createAccountresponse);

        // Response Code: 201
        Assert.assertEquals(
                body.get("responseCode").asInt(), 201, "Unexpected responseCode in response body");

        // Response Message: User created!
        Assert.assertEquals(
                body.get("message").asString(),
                ApiMessages.USER_CREATED,
                "Unexpected response message in response body");

        // Last checks
        Response getUserResponse = accountApiClient.getUserByEmail(user.email());

        Assert.assertEquals(getUserResponse.statusCode(), 200, "Unexpected HTTP status code");

        UserDetailsResponseDto actual =
                ApiResponseParser.extract(getUserResponse, UserDetailsResponseDto.class);

        Assert.assertEquals(actual.responseCode(), 200, "Unexpected responseCode");

        UserApiAssertions.assertMatches(user, actual.user());
    }

    @Test
    @Description("API 14: GET user account detail by email")
    public void shouldGetUserAccountDetailByEmail() {
        User user = accounts.createUser();

        // API URL: https://automationexercise.com/api/getUserDetailByEmail
        // Request Method: GET
        // Request Parameters: email
        Response getUserResponse = accountApiClient.getUserByEmail(user.email());

        Assert.assertEquals(getUserResponse.statusCode(), 200, "Unexpected HTTP status code");

        UserDetailsResponseDto body =
                ApiResponseParser.extract(getUserResponse, UserDetailsResponseDto.class);

        // Response Code: 200
        Assert.assertEquals(body.responseCode(), 200, "Unexpected responseCode");

        // Response JSON: User Detail
        UserApiAssertions.assertMatches(user, body.user());
    }

    @Test
    @Description("API 13: PUT METHOD To Update User Account")
    public void shouldUpdateExistingUserAccount() {
        User currentUser = accounts.createUser();
        User updatedUser = UserFactory.updatedUserFrom(currentUser);

        // API URL: https://automationexercise.com/api/updateAccount
        // Request Method: PUT
        // Request Parameters: name, email, password, title (for example: Mr, Mrs,
        // Miss), birth_date, birth_month, birth_year, firstname, lastname, company,
        // address1, address2, country, zipcode, state, city, mobile_number
        Response updateUserResonse = accountApiClient.updateAccount(updatedUser);

        // Response Code: 200
        Assert.assertEquals(updateUserResonse.statusCode(), 200, "Unexpected HTTP status code");

        JsonNode updateUserbody = ApiResponseParser.extractJson(updateUserResonse);

        Assert.assertEquals(
                updateUserbody.get("responseCode").asInt(),
                200,
                "Unexpected responseCode in response body");

        // Response Message: User updated!
        Assert.assertEquals(
                updateUserbody.get("message").asString(),
                ApiMessages.USER_UPDATED,
                "Unexpected response message in response body");

        // Last checks
        Response getUserResponse = accountApiClient.getUserByEmail(currentUser.email());
        UserDetailsResponseDto getUserBody =
                ApiResponseParser.extract(getUserResponse, UserDetailsResponseDto.class);

        // Response Code: 200
        Assert.assertEquals(getUserBody.responseCode(), 200, "Unexpected responseCode");
        UserApiAssertions.assertMatches(updatedUser, getUserBody.user());
    }

    @Test
    @Description("API 12: DELETE METHOD To Delete User Account")
    public void shouldDeleteExistingUserAccount() {
        User user = accounts.createUser();
        // API URL: https://automationexercise.com/api/deleteAccount
        // Request Method: DELETE
        // Request Parameters: email, password
        Response deleteAccountResponse = accountApiClient.deleteAccount(user);

        // Response Code: 200
        Assert.assertEquals(deleteAccountResponse.statusCode(), 200, "Unexpected HTTP status code");

        JsonNode updateUserbody = ApiResponseParser.extractJson(deleteAccountResponse);

        Assert.assertEquals(
                updateUserbody.get("responseCode").asInt(),
                200,
                "Unexpected responseCode in response body");
        // Response Message: Account deleted!
        Assert.assertEquals(
                updateUserbody.get("message").asString(),
                ApiMessages.ACCOUNT_DELETED,
                "Unexpected message in response body");
    }

    @Test
    @Description("API 7: POST To Verify Login with valid details")
    public void shouldVerifyLoginWithValidEmailAndPassword() {
        User user = accounts.createUser();

        // API URL: https://automationexercise.com/api/verifyLogin
        // Request Method: POST
        // Request Parameters: email, password
        Response verifyLoginResponse = accountApiClient.postToVerifyLogin(user);

        // Response Code: 200
        Assert.assertEquals(verifyLoginResponse.statusCode(), 200, "Unexpected HTTP status code");

        JsonNode verifyLoginBody = ApiResponseParser.extractJson(verifyLoginResponse);

        Assert.assertEquals(
                verifyLoginBody.get("responseCode").asInt(),
                200,
                "Unexpected responseCode in response body");
        // Response Message: User exists!
        Assert.assertEquals(
                verifyLoginBody.get("message").asString(),
                ApiMessages.ACCOUNT_EXISTS,
                "Unexpected message in response body");
    }

    @Test
    @Description("API 8: POST To Verify Login without email parameter")
    public void shouldVerifyLoginOnlyWithPassword() {
        User user = accounts.createUser();
        // API URL: https://automationexercise.com/api/verifyLogin
        // Request Method: POST
        // Request Parameter: password
        Response verifyLoginResponse =
                accountApiClient.postToVerifyLoginOnlyWithPassword(user.password());

        Assert.assertEquals(verifyLoginResponse.statusCode(), 200, "Unexpected HTTP status code");

        JsonNode verifyLoginBody = ApiResponseParser.extractJson(verifyLoginResponse);

        // Response Code: 400
        Assert.assertEquals(
                verifyLoginBody.get("responseCode").asInt(),
                400,
                "Unexpected responseCode in response body");

        // Response Message: Bad request, email or password parameter is missing in POST
        // request.
        Assert.assertEquals(
                verifyLoginBody.get("message").asString(),
                ApiMessages.BAD_REQUEST,
                "Unexpected message in response body");
    }

    @Test
    @Description("API 9: DELETE To Verify Login")
    public void shouldRejectDeleteRequestToVerifyLogin() {

        // API URL: https://automationexercise.com/api/verifyLogin
        // Request Method: DELETE
        Response deleteToVerifyLogin = accountApiClient.deleteToVerifyLogin();

        Assert.assertEquals(deleteToVerifyLogin.statusCode(), 200, "Unexpected HTTP status code");

        JsonNode verifyLoginBody = ApiResponseParser.extractJson(deleteToVerifyLogin);

        // Response Code: 405
        Assert.assertEquals(
                verifyLoginBody.get("responseCode").asInt(),
                405,
                "Unexpected responseCode in response body");

        // Response Message: This request method is not supported.
        Assert.assertEquals(
                verifyLoginBody.get("message").asString(),
                ApiMessages.REQUEST_METHOD_IS_NOT_SUPPORTED,
                "Unexpected message in response body");
    }

    @Test
    @Description("API 10: POST To Verify Login with invalid details")
    public void shouldRejectLoginWithInvalidDetails() {

        User user1 = accounts.createUser();
        User user2 = accounts.createUser();

        // API URL: https://automationexercise.com/api/verifyLogin
        // Request Method: POST
        // Request Parameters: email, password (invalid values)
        Response verifyLoginResponse = accountApiClient.postToVerifyLogin(user1, user2.password());

        Assert.assertEquals(verifyLoginResponse.statusCode(), 200, "Unexpected HTTP status code");

        JsonNode verifyLoginBody = ApiResponseParser.extractJson(verifyLoginResponse);

        // Response Code: 404
        Assert.assertEquals(
                verifyLoginBody.get("responseCode").asInt(),
                404,
                "Unexpected responseCode in response body");

        // Response Message: User not found!
        Assert.assertEquals(
                verifyLoginBody.get("message").asString(),
                ApiMessages.USER_NOT_FOUND,
                "Unexpected message in response body");
    }
}
