package com.shangin.automationexercise.tests.ui.account;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.shangin.automationexercise.base.BaseTest;
import com.shangin.automationexercise.constants.UiMessages;
import com.shangin.automationexercise.factories.UserFactory;
import com.shangin.automationexercise.model.User;
import com.shangin.automationexercise.pages.AccountCreatedPage;
import com.shangin.automationexercise.pages.AccountDeletedPage;
import com.shangin.automationexercise.pages.AccountInformationPage;
import com.shangin.automationexercise.pages.HomePage;
import com.shangin.automationexercise.pages.SignupLoginPage;

import io.qameta.allure.Description;

@Test(groups = "ui")
public class RegistrationTest extends BaseTest {
    @Test @Description("Test Case 1: Register User")
    public void shouldRegisterNewUser() {

        User newUser = UserFactory.randomUser();

        // 1. Launch browser
        // 2. Navigate to url 'http://automationexercise.com'
        HomePage homePage = HomePage.open();

        // 3. Verify that home page is visible successfully
        Assert.assertTrue(homePage.isLoaded(), "Home page should be loaded");

        // 4. Click on 'Signup / Login' button
        SignupLoginPage loginPage = homePage.header().openSignupLoginPage();

        // 5. Verify 'New User Signup!' is visible
        Assert.assertEquals(loginPage.getUserSignupHeader(), UiMessages.NEW_USER_SIGNUP);

        // 6. Enter name and email address
        // 7. Click 'Signup' button
        AccountInformationPage accountInformationPage = loginPage.register(newUser);

        // 8. Verify that 'ENTER ACCOUNT INFORMATION' is visible
        Assert.assertEquals(accountInformationPage.getPageTitle(), UiMessages.ENTER_ACCOUNT_INFORMATION);

        // 9. Fill details: Title, Name, Email, Password, Date of birth
        // 10. Select checkbox 'Sign up for our newsletter!'
        // 11. Select checkbox 'Receive special offers from our partners!'
        // 12. Fill details: First name, Last name, Company, Address, Address2, Country,
        // State, City, Zipcode, Mobile Number
        // 13. Click 'Create Account button'
        AccountCreatedPage accountCreatedPage = accountInformationPage.createAccount(newUser);

        // 14. Verify that 'ACCOUNT CREATED!' is visible
        Assert.assertEquals(accountCreatedPage.getAccountCreatedMessage(), UiMessages.ACCOUNT_CREATED);

        // 15. Click 'Continue' button
        homePage = accountCreatedPage.continueShopping();

        // 16. Verify that 'Logged in as username' is visible
        Assert.assertEquals(
                homePage.header().getLoggedInUserName(),
                newUser.firstName(),
                "Incorrect user is logged in");

        // 17. Click 'Delete Account' button
        AccountDeletedPage accountDeletedPage = homePage.header().deleteAccount();

        // 18. Verify that 'ACCOUNT DELETED!' is visible and click 'Continue' button
        Assert.assertEquals(accountDeletedPage.getAccountDeletedMessage(), UiMessages.ACCOUNT_DELETED);

        homePage = accountDeletedPage.continueShopping();
        //
        Assert.assertTrue(homePage.isOpened());
        Assert.assertTrue(homePage.isLoaded());

    }

}
