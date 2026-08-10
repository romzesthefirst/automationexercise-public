package com.shangin.automationexercise.tests.ui.account;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.shangin.automationexercise.base.BaseTest;
import com.shangin.automationexercise.constants.UiMessages;
import com.shangin.automationexercise.factories.UserFactory;
import com.shangin.automationexercise.model.User;
import com.shangin.automationexercise.pages.AccountDeletedPage;
import com.shangin.automationexercise.pages.HomePage;
import com.shangin.automationexercise.pages.SignupLoginPage;
import com.shangin.automationexercise.steps.UserRegistrationResult;
import com.shangin.automationexercise.steps.UserSteps;

import io.qameta.allure.Description;

public class LoginTest extends BaseTest {

    @Test @Description("Test Case 2: Login User with correct email and password")
    public void shouldLoginWithValidCredentials() {
        // 0. Create new user
        UserRegistrationResult result = UserSteps.registerNewUserWithLogout();
        User user = result.user();
        

        // 1. Launch browser
        // 2. Navigate to url 'http://automationexercise.com'
        HomePage homePage = result.homePage();
        // 3. Verify that home page is visible successfully
        Assert.assertTrue(homePage.isLoaded());
        
        // 4. Click on 'Signup / Login' button
        SignupLoginPage signupLoginPage = homePage.header().openSignupLoginPage();

        // 5. Verify 'Login to your account' is visible
        Assert.assertEquals(
                signupLoginPage.getUserLoginHeader(),
                UiMessages.LOGIN_TO_YOUR_ACCOUNT,
                "Login form header is incorrect");

        // 6. Enter correct email address and password
        // 7. Click 'login' button
        homePage = signupLoginPage.successLogin(user);

        // 8. Verify that 'Logged in as username' is visible
        Assert.assertEquals(
                homePage.header().getLoggedInUserName(),
                user.firstName(),
                "Incorrect user is logged in");

        // 9. Click 'Delete Account' button
        AccountDeletedPage accountDeletedPage = homePage.header().deleteAccount();

        // 10. Verify that 'ACCOUNT DELETED!' is visible
        Assert.assertEquals(
                accountDeletedPage.getAccountDeletedMessage(),
                UiMessages.ACCOUNT_DELETED,
                "Account deletion confirmation is incorrect");
    }

    @Test @Description("Test Case 3: Login User with incorrect email and password")
    public void shouldNotLoginWithIncorrectCredentials() {
        User newUser = UserFactory.randomUser();

        // 1. Launch browser
        // 2. Navigate to url 'http://automationexercise.com'
        HomePage homePage = HomePage.open();

        // 3. Verify that home page is visible successfully
        Assert.assertTrue(homePage.isLoaded(), "Home page should be loaded");

        // 4. Click on 'Signup / Login' button
        SignupLoginPage loginPage = homePage.header().openSignupLoginPage();

        // 5. Verify 'Login to your account' is visible
        Assert.assertEquals(
                loginPage.getUserLoginHeader(),
                UiMessages.LOGIN_TO_YOUR_ACCOUNT,
                "Login form header is incorrect");

        // 6. Enter incorrect email address and password
        // 7. Click 'login' button
        loginPage.failedLogin(newUser);

        // 8. Verify error 'Your email or password is incorrect!' is visible
        Assert.assertEquals(loginPage.getLoginErrorMessage(), UiMessages.INCORRECT_EMAIL_PASSWORD);

    }

    @Test @Description("Test Case 4: Logout User")
    public void userCanLogOut() {
        // 0. Create new user
        UserRegistrationResult result = UserSteps.registerNewUserWithLogout();
        User user = result.user();

        // 1. Launch browser
        // 2. Navigate to url 'http://automationexercise.com'
        HomePage homePage = result.homePage();
        
        // 3. Verify that home page is visible successfully
        Assert.assertTrue(homePage.isLoaded());
        
        // 4. Click on 'Signup / Login' button
        SignupLoginPage signupLoginPage = homePage.header().openSignupLoginPage();

        // 5. Verify 'Login to your account' is visible
        Assert.assertEquals(
                signupLoginPage.getUserLoginHeader(),
                UiMessages.LOGIN_TO_YOUR_ACCOUNT,
                "Login form header is incorrect");

        // 6. Enter correct email address and password
        // 7. Click 'login' button
        homePage = signupLoginPage.successLogin(user);

        // 8. Verify that 'Logged in as username' is visible
        Assert.assertEquals(
                homePage.header().getLoggedInUserName(),
                user.firstName(),
                "Incorrect user is logged in");

        // 9. Click 'Logout' button
        signupLoginPage = homePage.header().logout();

        // 10. Verify that user is navigated to login page
        Assert.assertTrue(signupLoginPage.isOpened(), "User should be navigated to Login page");
    }

    @Test @Description("Test Case 5: Register User with existing email")
    public void userCantRegisterWithExistiongEmal() {
        // 0. Create new user
        UserRegistrationResult result = UserSteps.registerNewUserWithLogout();
        User user = result.user();

        // 1. Launch browser
        // 2. Navigate to url 'http://automationexercise.com'
        HomePage homePage = result.homePage();
        
        // 3. Verify that home page is visible successfully
        Assert.assertTrue(homePage.isLoaded());
        
        // 4. Click on 'Signup / Login' button
        SignupLoginPage signupLoginPage = homePage.header().openSignupLoginPage();

        // 5. Verify 'New User Signup!' is visible
        Assert.assertEquals(
                signupLoginPage.getUserSignupHeader(),
                UiMessages.NEW_USER_SIGNUP,
                "Login form header is incorrect");

        // 6. Enter name and already registered email address
        // 7. Click 'Signup' button
        signupLoginPage.register(user);

        // 8. Verify error 'Email Address already exist!' is visible
        Assert.assertEquals(signupLoginPage.getSignUpErrorMessage(), UiMessages.EMAIL_ALREADY_EXISTS);

    }
}
