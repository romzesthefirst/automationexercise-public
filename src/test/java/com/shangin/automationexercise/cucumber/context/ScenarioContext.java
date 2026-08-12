package com.shangin.automationexercise.cucumber.context;

import com.shangin.automationexercise.model.User;

public class ScenarioContext {

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}