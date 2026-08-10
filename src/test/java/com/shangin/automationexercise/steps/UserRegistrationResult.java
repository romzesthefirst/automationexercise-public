package com.shangin.automationexercise.steps;

import com.shangin.automationexercise.model.User;
import com.shangin.automationexercise.pages.HomePage;

public record UserRegistrationResult(
        User user,
        HomePage homePage
) {}
