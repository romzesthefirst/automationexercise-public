package com.shangin.automationexercise.api.clients;

import static io.restassured.RestAssured.given;

import com.shangin.automationexercise.config.ConfigReader;
import com.shangin.automationexercise.mappers.UserApiMapper;
import com.shangin.automationexercise.model.User;

import io.restassured.response.Response;

public class AccountApiClient {

    private static final String CREATE_ACCOUNT_ENDPOINT = "/createAccount";
    private static final String GET_ACCOUNT_DETAIL_BY_EMAIL_ENDPOINT = "/getUserDetailByEmail";
    private static final String UPDATE_ACCOUNT_ENDPOINT = "/updateAccount";
    private static final String VERIFY_ACCOUNT_LOGIN_ENDPOINT = "/verifyLogin";
    private static final String DELETE_ACCOUNT_ENDPOINT = "/deleteAccount";
    
    private static final String POST_CONTENT_TYPE = "application/x-www-form-urlencoded";
    
    public Response createAccount(User user) {
        return given()
                .baseUri(ConfigReader.getApiBaseUrl())
                .contentType(POST_CONTENT_TYPE)
                .formParams(UserApiMapper.toCreateAccountForm(user))
                .when()
                .post(CREATE_ACCOUNT_ENDPOINT);
    }
    
    public Response getUserByEmail(String email) {
        return given()
                .baseUri(ConfigReader.getApiBaseUrl())
                .queryParam("email", email)
                .when()
                .get(GET_ACCOUNT_DETAIL_BY_EMAIL_ENDPOINT);
    }
    
    public Response updateAccount(User newUserData) {
        return given()
                .baseUri(ConfigReader.getApiBaseUrl())
                .contentType(POST_CONTENT_TYPE)
                .formParams(UserApiMapper.toCreateAccountForm(newUserData))
                .when()
                .put(UPDATE_ACCOUNT_ENDPOINT);
    }
    
    public Response postToVerifyLogin(User user) {
        return given()
                .baseUri(ConfigReader.getApiBaseUrl())
                .contentType(POST_CONTENT_TYPE)
                .formParam("email", user.email())
                .formParam("password", user.password())
                .when()
                .post(VERIFY_ACCOUNT_LOGIN_ENDPOINT);
    }
    
    public Response postToVerifyLogin(User user, String password) {
        return given()
                .baseUri(ConfigReader.getApiBaseUrl())
                .contentType(POST_CONTENT_TYPE)
                .formParam("email", user.email())
                .formParam("password", password)
                .when()
                .post(VERIFY_ACCOUNT_LOGIN_ENDPOINT);
    }
    
    public Response postToVerifyLoginOnlyWithPassword(String password) {
        return given()
                .baseUri(ConfigReader.getApiBaseUrl())
                .contentType(POST_CONTENT_TYPE)
                .formParam("password", password)
                .when()
                .post(VERIFY_ACCOUNT_LOGIN_ENDPOINT);
    }
    
    public Response deleteToVerifyLogin() {
        return given()
                .baseUri(ConfigReader.getApiBaseUrl())
                .when()
                .delete(VERIFY_ACCOUNT_LOGIN_ENDPOINT);
    }
    
    public Response deleteAccount(User user) {
        return given()
                .baseUri(ConfigReader.getApiBaseUrl())
                .contentType(POST_CONTENT_TYPE)
                .formParam("email", user.email())
                .formParam("password", user.password())
                .when()
                .delete(DELETE_ACCOUNT_ENDPOINT);
    }
    
}
