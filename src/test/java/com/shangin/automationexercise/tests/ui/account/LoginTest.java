package com.shangin.automationexercise.tests.ui.account;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.shangin.automationexercise.base.AccountUiTestBase;
import com.shangin.automationexercise.constants.UiMessages;
import com.shangin.automationexercise.factories.UserFactory;
import com.shangin.automationexercise.model.User;
import com.shangin.automationexercise.pages.AccountDeletedPage;
import com.shangin.automationexercise.pages.HomePage;
import com.shangin.automationexercise.pages.SignupLoginPage;

import io.qameta.allure.Description;

@Test(groups = "ui")
public class LoginTest extends AccountUiTestBase {

    @Test @Description("Test Case 2: Login User with correct email and password")
    public void shouldLoginWithValidCredentials() {

        User user = accounts.createUser();

        HomePage homePage = HomePage.open();

        SignupLoginPage signupLoginPage = homePage.header().openSignupLoginPage();

        Assert.assertEquals(
                signupLoginPage.getUserLoginHeader(),
                UiMessages.LOGIN_TO_YOUR_ACCOUNT,
                "Login form header is incorrect");

        homePage = signupLoginPage.successLogin(user);

        Assert.assertEquals(
                homePage.header().getLoggedInUserName(),
                user.firstName(),
                "Incorrect user is logged in");

        AccountDeletedPage accountDeletedPage = homePage.header().deleteAccount();

        Assert.assertEquals(
                accountDeletedPage.getAccountDeletedMessage(),
                UiMessages.ACCOUNT_DELETED,
                "Account deletion confirmation is incorrect");
    }

    @Test @Description("Test Case 3: Login User with incorrect email and password")
    public void shouldNotLoginWithIncorrectCredentials() {

        User newUser = UserFactory.randomUser();

        HomePage homePage = HomePage.open();

        SignupLoginPage loginPage = homePage.header().openSignupLoginPage();

        Assert.assertEquals(
                loginPage.getUserLoginHeader(),
                UiMessages.LOGIN_TO_YOUR_ACCOUNT,
                "Login form header is incorrect");

        loginPage.attemptToLogin(newUser);

        Assert.assertEquals(loginPage.getLoginErrorMessage(), UiMessages.INCORRECT_EMAIL_PASSWORD);

    }

    @Test @Description("Test Case 4: Logout User")
    public void userCanLogOut() {

        User user = accounts.createUser();

        HomePage homePage = HomePage.open();

        SignupLoginPage signupLoginPage = homePage.header().openSignupLoginPage();

        Assert.assertEquals(
                signupLoginPage.getUserLoginHeader(),
                UiMessages.LOGIN_TO_YOUR_ACCOUNT,
                "Login form header is incorrect");

        homePage = signupLoginPage.successLogin(user);

        Assert.assertEquals(
                homePage.header().getLoggedInUserName(),
                user.firstName(),
                "Incorrect user is logged in");

        signupLoginPage = homePage.header().logout();

        Assert.assertTrue(signupLoginPage.isLoaded(), "User should be navigated to Login page");
    }
}
