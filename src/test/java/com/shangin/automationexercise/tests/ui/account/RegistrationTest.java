package com.shangin.automationexercise.tests.ui.account;

import com.shangin.automationexercise.base.AccountUiTestBase;
import com.shangin.automationexercise.constants.UiMessages;
import com.shangin.automationexercise.model.User;
import com.shangin.automationexercise.pages.AccountCreatedPage;
import com.shangin.automationexercise.pages.AccountDeletedPage;
import com.shangin.automationexercise.pages.AccountInformationPage;
import com.shangin.automationexercise.pages.HomePage;
import com.shangin.automationexercise.pages.SignupLoginPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = "ui")
@Epic("Automation Exercise")
@Feature("Account")
public class RegistrationTest extends AccountUiTestBase {
    @Test
    @Description("Test Case 1: Register User")
    public void shouldRegisterNewUser() {

        HomePage homePage = HomePage.open();

        Assert.assertTrue(homePage.isLoaded(), "Home page should be loaded");

        SignupLoginPage loginPage = homePage.header().openSignupLoginPage();

        Assert.assertEquals(loginPage.getUserSignupHeader(), UiMessages.NEW_USER_SIGNUP);

        User newUser = accounts.newUser();
        AccountInformationPage accountInformationPage = loginPage.register(newUser);

        Assert.assertEquals(
                accountInformationPage.getPageTitle(), UiMessages.ENTER_ACCOUNT_INFORMATION);

        AccountCreatedPage accountCreatedPage = accountInformationPage.createAccount(newUser);

        Assert.assertEquals(
                accountCreatedPage.getAccountCreatedMessage(), UiMessages.ACCOUNT_CREATED);

        homePage = accountCreatedPage.continueShopping();

        Assert.assertEquals(
                homePage.header().getLoggedInUserName(),
                newUser.firstName(),
                "Incorrect user is logged in");

        AccountDeletedPage accountDeletedPage = homePage.header().deleteAccount();

        Assert.assertEquals(
                accountDeletedPage.getAccountDeletedMessage(), UiMessages.ACCOUNT_DELETED);

        homePage = accountDeletedPage.continueShopping();
        Assert.assertTrue(homePage.isOpened());
        Assert.assertTrue(homePage.isLoaded());
    }

    @Test
    @Description("Test Case 5: Register User with existing email")
    public void shouldRejectRegistrationWithExistingEmail() {

        User user = accounts.createUser();

        HomePage homePage = HomePage.open();

        Assert.assertTrue(homePage.isLoaded());

        SignupLoginPage signupLoginPage = homePage.header().openSignupLoginPage();

        Assert.assertEquals(
                signupLoginPage.getUserSignupHeader(),
                UiMessages.NEW_USER_SIGNUP,
                "Login form header is incorrect");

        signupLoginPage.attemptToRegister(user);

        Assert.assertEquals(
                signupLoginPage.getSignUpErrorMessage(), UiMessages.EMAIL_ALREADY_EXISTS);
    }
}
