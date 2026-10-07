package com.shangin.automationexercise.components;

import com.shangin.automationexercise.base.BaseComponent;
import com.shangin.automationexercise.model.User;
import com.shangin.automationexercise.pages.AccountDeletedPage;
import com.shangin.automationexercise.pages.ApiListPage;
import com.shangin.automationexercise.pages.CartPage;
import com.shangin.automationexercise.pages.ContactUsPage;
import com.shangin.automationexercise.pages.HomePage;
import com.shangin.automationexercise.pages.ProductsPage;
import com.shangin.automationexercise.pages.SignupLoginPage;
import com.shangin.automationexercise.pages.TestCasesPage;
import org.openqa.selenium.By;

public class HeaderComponent extends BaseComponent {

    public HeaderComponent(By rootLocator) {
        super(rootLocator);
    }

    // Logo
    private static final By LOGO = By.cssSelector(".logo a");

    // Menu
    private static final By HOME_LINK = By.cssSelector(".shop-menu a[href='/']");
    private static final By PRODUCTS_LINK = By.cssSelector(".shop-menu a[href='/products']");
    private static final By CART_LINK = By.cssSelector(".shop-menu a[href='/view_cart']");
    private static final By SIGNUP_LOGIN_LINK = By.cssSelector(".shop-menu a[href='/login']");
    private static final By TEST_CASES_LINK = By.cssSelector(".shop-menu a[href='/test_cases']");
    private static final By API_TESTING_LINK = By.cssSelector(".shop-menu a[href='/api_list']");
    private static final By CONTACT_US_LINK = By.cssSelector(".shop-menu a[href='/contact_us']");

    // After login
    private static final By LOGOUT_LINK = By.cssSelector(".shop-menu a[href='/logout']");
    private static final By DELETE_ACCOUNT_LINK =
            By.cssSelector(".shop-menu a[href='/delete_account']");
    private static final By LOGGED_IN_AS = By.xpath("//a[i[contains(@class,'fa-user')]]");
    private static final By LOGGED_IN_AS_NAME = By.xpath("//a[i[contains(@class,'fa-user')]]/b");

    public HomePage clickLogo() {
        navigate(LOGO);
        HomePage homePage = new HomePage();
        homePage.waitUntilLoaded();
        return homePage;
    }

    public HomePage openHomePage() {
        navigate(HOME_LINK);
        HomePage homePage = new HomePage();
        homePage.waitUntilLoaded();
        return homePage;
    }

    public ProductsPage openProducts() {
        navigate(PRODUCTS_LINK);
        ProductsPage productsPage = new ProductsPage();
        productsPage.waitUntilLoaded();
        return productsPage;
    }

    public CartPage openCart() {
        navigate(CART_LINK);
        CartPage cartPage = new CartPage();
        cartPage.waitUntilLoaded();
        return cartPage;
    }

    public SignupLoginPage openSignupLoginPage() {
        navigate(SIGNUP_LOGIN_LINK);
        SignupLoginPage signupLoginPage = new SignupLoginPage();
        signupLoginPage.waitUntilLoaded();
        return signupLoginPage;
    }

    public TestCasesPage openTestCases() {
        navigate(TEST_CASES_LINK);
        TestCasesPage testCasesPage = new TestCasesPage();
        testCasesPage.waitUntilLoaded();
        return testCasesPage;
    }

    public ApiListPage openApiTesting() {
        navigate(API_TESTING_LINK);
        ApiListPage apiListPage = new ApiListPage();
        apiListPage.waitUntilLoaded();
        return apiListPage;
    }

    public ContactUsPage openContactUs() {
        navigate(CONTACT_US_LINK);
        ContactUsPage contactUsPage = new ContactUsPage();
        contactUsPage.waitUntilLoaded();
        return contactUsPage;
    }

    public AccountDeletedPage deleteAccount() {
        click(DELETE_ACCOUNT_LINK);
        AccountDeletedPage accountDeletedPage = new AccountDeletedPage();
        accountDeletedPage.waitUntilLoaded();
        return accountDeletedPage;
    }

    public SignupLoginPage logout() {
        click(LOGOUT_LINK);
        SignupLoginPage signupLoginPage = new SignupLoginPage();
        signupLoginPage.waitUntilLoaded();
        return signupLoginPage;
    }

    public String getLoggedInUserName() {
        return waitUntilVisible(LOGGED_IN_AS_NAME).getText();
    }

    public boolean isLoggedInAs(User user) {
        return waitUntilVisible(LOGGED_IN_AS_NAME).getText().equals(user.firstName());
    }

    public String getLoggedInAsText() {
        return waitUntilVisible(LOGGED_IN_AS).getText();
    }
}
