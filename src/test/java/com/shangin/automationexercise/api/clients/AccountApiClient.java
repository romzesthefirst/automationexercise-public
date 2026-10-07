package com.shangin.automationexercise.api.clients;

import com.shangin.automationexercise.api.specs.ApiSpecifications;
import com.shangin.automationexercise.mappers.UserApiMapper;
import com.shangin.automationexercise.model.User;
import io.restassured.response.Response;

public class AccountApiClient {

    private static final String CREATE_ACCOUNT_ENDPOINT = "/createAccount";
    private static final String GET_ACCOUNT_DETAIL_BY_EMAIL_ENDPOINT = "/getUserDetailByEmail";
    private static final String UPDATE_ACCOUNT_ENDPOINT = "/updateAccount";
    private static final String VERIFY_ACCOUNT_LOGIN_ENDPOINT = "/verifyLogin";
    private static final String DELETE_ACCOUNT_ENDPOINT = "/deleteAccount";

    public Response createAccount(User user) {
        return ApiSpecifications.formRequest()
                .formParams(UserApiMapper.toCreateAccountForm(user))
                .when()
                .post(CREATE_ACCOUNT_ENDPOINT);
    }

    public Response getUserByEmail(String email) {
        return ApiSpecifications.defaultRequest()
                .queryParam("email", email)
                .when()
                .get(GET_ACCOUNT_DETAIL_BY_EMAIL_ENDPOINT);
    }

    public Response updateAccount(User newUserData) {
        return ApiSpecifications.formRequest()
                .formParams(UserApiMapper.toCreateAccountForm(newUserData))
                .when()
                .put(UPDATE_ACCOUNT_ENDPOINT);
    }

    public Response postToVerifyLogin(User user) {
        return ApiSpecifications.formRequest()
                .formParam("email", user.email())
                .formParam("password", user.password())
                .when()
                .post(VERIFY_ACCOUNT_LOGIN_ENDPOINT);
    }

    public Response postToVerifyLogin(User user, String password) {
        return ApiSpecifications.formRequest()
                .formParam("email", user.email())
                .formParam("password", password)
                .when()
                .post(VERIFY_ACCOUNT_LOGIN_ENDPOINT);
    }

    public Response postToVerifyLoginOnlyWithPassword(String password) {
        return ApiSpecifications.formRequest()
                .formParam("password", password)
                .when()
                .post(VERIFY_ACCOUNT_LOGIN_ENDPOINT);
    }

    public Response deleteToVerifyLogin() {
        return ApiSpecifications.defaultRequest().when().delete(VERIFY_ACCOUNT_LOGIN_ENDPOINT);
    }

    public Response deleteAccount(User user) {
        return ApiSpecifications.formRequest()
                .formParam("email", user.email())
                .formParam("password", user.password())
                .when()
                .delete(DELETE_ACCOUNT_ENDPOINT);
    }
}
