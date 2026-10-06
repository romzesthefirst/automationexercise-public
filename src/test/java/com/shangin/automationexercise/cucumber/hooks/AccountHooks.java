package com.shangin.automationexercise.cucumber.hooks;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.shangin.automationexercise.api.clients.AccountApiClient;
import com.shangin.automationexercise.api.support.OwnedAccounts;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;

public class AccountHooks {
    private final OwnedAccounts ownedAccounts;
    private final AccountApiClient accountApiClient;

    public AccountHooks(OwnedAccounts ownedAccounts, AccountApiClient accountApiClient) {
        this.ownedAccounts = ownedAccounts;
        this.accountApiClient = accountApiClient;
    }

    @After(order = 0)
    public void cleanupUser(Scenario scenario) {
        try {
            ownedAccounts.cleanup(accountApiClient);
        } catch (AssertionError failure) {
            StringWriter details = new StringWriter();
            failure.printStackTrace(new PrintWriter(details));
            scenario.attach(details.toString(), "text/plain", "Account cleanup failure");
            if (!scenario.isFailed()) {
                throw failure;
            }
        }
    }
}
