package com.shangin.automationexercise.tests.ui.account;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.shangin.automationexercise.api.support.ApiCleanupHelper;
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

        HomePage homePage = HomePage.open();

        Assert.assertTrue(homePage.isLoaded(), "Home page should be loaded");

        SignupLoginPage loginPage = homePage.header().openSignupLoginPage();

        Assert.assertEquals(loginPage.getUserSignupHeader(), UiMessages.NEW_USER_SIGNUP);

        User newUser = UserFactory.randomUser();
        AccountInformationPage accountInformationPage = loginPage.register(newUser);

        Assert.assertEquals(
                accountInformationPage.getPageTitle(),
                UiMessages.ENTER_ACCOUNT_INFORMATION);

        AccountCreatedPage accountCreatedPage = accountInformationPage.createAccount(newUser);

        Assert.assertEquals(
                accountCreatedPage.getAccountCreatedMessage(),
                UiMessages.ACCOUNT_CREATED);

        homePage = accountCreatedPage.continueShopping();

        Assert.assertEquals(
                homePage.header().getLoggedInUserName(),
                newUser.firstName(),
                "Incorrect user is logged in");

        AccountDeletedPage accountDeletedPage = homePage.header().deleteAccount();

        Assert.assertEquals(
                accountDeletedPage.getAccountDeletedMessage(),
                UiMessages.ACCOUNT_DELETED);

        homePage = accountDeletedPage.continueShopping();
        Assert.assertTrue(homePage.isOpened());
        Assert.assertTrue(homePage.isLoaded());
    }

    @Test @Description("Test Case 5: Register User with existing email")
    public void userCantRegisterWithExistiongEmal() {

        User user = apiUserSteps.createUser();

        try {

            HomePage homePage = HomePage.open();

            Assert.assertTrue(homePage.isLoaded());

            SignupLoginPage signupLoginPage = homePage.header().openSignupLoginPage();

            Assert.assertEquals(
                    signupLoginPage.getUserSignupHeader(),
                    UiMessages.NEW_USER_SIGNUP,
                    "Login form header is incorrect");

            signupLoginPage.register(user);

            Assert.assertEquals(
                    signupLoginPage.getSignUpErrorMessage(),
                    UiMessages.EMAIL_ALREADY_EXISTS);

        } finally {

            ApiCleanupHelper.deleteAccountQuietly(accountApiClient, user);
        }
    }

}
