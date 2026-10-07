package com.shangin.automationexercise.constants;

public final class ApiMessages {

    private ApiMessages() {}

    // common
    public static final String REQUEST_METHOD_IS_NOT_SUPPORTED =
            "This request method is not supported.";

    // searchProduct
    public static final String SEARCH_PRODUCT_PARAMETER_IS_MISSING =
            "Bad request, search_product parameter is missing in POST request.";

    // user
    public static final String USER_CREATED = "User created!";
    public static final String USER_UPDATED = "User updated!";
    public static final String ACCOUNT_DELETED = "Account deleted!";
    public static final String ACCOUNT_EXISTS = "User exists!";
    public static final String BAD_REQUEST =
            "Bad request, email or password parameter is missing in POST request.";
    public static final String USER_NOT_FOUND = "User not found!";
}
