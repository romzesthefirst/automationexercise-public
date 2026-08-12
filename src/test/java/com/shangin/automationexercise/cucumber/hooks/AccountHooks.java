package com.shangin.automationexercise.cucumber.hooks;

import com.shangin.automationexercise.api.clients.AccountApiClient;
import com.shangin.automationexercise.api.support.ApiCleanupHelper;
import com.shangin.automationexercise.cucumber.context.ScenarioContext;

import io.cucumber.java.After;

public class AccountHooks {

    private final ScenarioContext context;
    private final AccountApiClient accountApiClient;

    public AccountHooks(ScenarioContext context, AccountApiClient accountApiClient) {
        this.context = context;
        this.accountApiClient = accountApiClient;
    }

    @After("@user")
    public void cleanupUser() {
        if (context.getUser() != null) {
            ApiCleanupHelper.deleteAccountQuietly(accountApiClient, context.getUser());
        }
    }
}
