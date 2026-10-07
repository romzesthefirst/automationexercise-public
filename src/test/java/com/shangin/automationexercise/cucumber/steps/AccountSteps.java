package com.shangin.automationexercise.cucumber.steps;

import com.shangin.automationexercise.cucumber.context.ScenarioContext;
import com.shangin.automationexercise.steps.ApiUserSteps;
import io.cucumber.java.en.Given;

public class AccountSteps {

    private final ScenarioContext context;
    private final ApiUserSteps apiUserSteps;

    public AccountSteps(ScenarioContext context, ApiUserSteps apiUserSteps) {
        this.context = context;
        this.apiUserSteps = apiUserSteps;
    }

    @Given("a registered user exists")
    public void registeredUserExists() {
        context.setUser(apiUserSteps.createUser());
    }

    @Given("credentials for an unregistered user")
    public void createUnregisteredUser() {
        context.setUser(context.newUser());
    }
}
