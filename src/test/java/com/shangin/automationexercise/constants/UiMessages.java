package com.shangin.automationexercise.constants;

public final class UiMessages {

    private UiMessages() {
    }

    // login
    public static final String LOGIN_TO_YOUR_ACCOUNT = "Login to your account";
    public static final String NEW_USER_SIGNUP = "New User Signup!";
    public static final String EMAIL_ALREADY_EXISTS = "Email Address already exist!";

    public static final String ENTER_ACCOUNT_INFORMATION = "ENTER ACCOUNT INFORMATION";
    public static final String ACCOUNT_CREATED = "ACCOUNT CREATED!";
    public static final String ACCOUNT_DELETED = "ACCOUNT DELETED!";
    public static final String INCORRECT_EMAIL_PASSWORD = "Your email or password is incorrect!";

    // home
    public static final String RECOMMENDED_ITEMS = "RECOMMENDED ITEMS";
    public static final String CAROUSEL_SUBTITLE
            = "Full-Fledged practice website for Automation Engineers";

    // contact_us
    public static final String SUCCESS_FEEDBACK_MESSAGE
            = "Success! Your details have been submitted successfully.";

    // products
    public static final String ALL_PPRODUCTS = "ALL PPRODUCTS";
    public static final String SEARCHED_PRODUCTS = "SEARCHED PRODUCTS";

    // product_details
    public static final String SUCCESS_REVIEW_MESSAGE = "Thank you for your review.";

    // footer
    public static final String SUBSCRIPTION = "SUBSCRIPTION";
    public static final String SUCCESS_SUBSCRIBE = "You have been successfully subscribed!";

    // payment
    public static final String ORDER_PLACED_SUCCESSFULLY
            = "Your order has been placed successfully!";

    // category_products
    private static final String CATEGORY_PRODUCTS_TITLE = "%s - %s PRODUCTS";

    public static String categoryProductsTitle(String category, String subcategory) {
        return CATEGORY_PRODUCTS_TITLE.formatted(category.toUpperCase(), subcategory.toUpperCase());
    }

    // brand_products
    private static final String BRAND_PRODUCTS_TITLE = "BRAND - %s PRODUCTS";

    public static String brandProductsTitle(String brand) {
        return BRAND_PRODUCTS_TITLE.formatted(brand.toUpperCase());
    }

    // invoice file
    private static final String INVOICE_TEXT
            = "Hi %s %s, Your total purchase amount is %s. Thank you";

    public static String invoiceText(String firstName, String secondName, String amount) {
        return INVOICE_TEXT.formatted(firstName, secondName, amount);
    }
}
