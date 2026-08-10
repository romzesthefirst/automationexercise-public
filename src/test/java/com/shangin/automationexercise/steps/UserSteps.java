package com.shangin.automationexercise.steps;

import com.shangin.automationexercise.factories.UserFactory;
import com.shangin.automationexercise.model.User;
import com.shangin.automationexercise.pages.HomePage;

public final class UserSteps {
	
	private UserSteps() {
	    throw new UnsupportedOperationException("Utility class");
	}
	
    public static User createRegisteredUser() {

        User user = UserFactory.randomUser();

        HomePage.open()
                .header()
                .openSignupLoginPage()
                .register(user)
                .createAccount(user);

        return user;
    }

    public static UserRegistrationResult registerNewUserWithLogout() {
        
        User user = UserFactory.randomUser();
        HomePage homePage = HomePage.open()
                .header()
                .openSignupLoginPage()
                .register(user)
                .createAccount(user)
                .continueShopping()
                .header()
                .logout()
                .header()
                .openHomePage();

        return new UserRegistrationResult(
                user,
                homePage
        );
    }

}
