package com.shangin.automationexercise.fixtures;

import com.shangin.automationexercise.api.clients.AccountApiClient;
import com.shangin.automationexercise.api.support.OwnedAccounts;
import com.shangin.automationexercise.model.User;
import com.shangin.automationexercise.steps.ApiUserSteps;
import io.qameta.allure.Allure;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.testng.ITestResult;

public final class AccountFixture {
    private final AccountApiClient accountApiClient;
    private final OwnedAccounts ownedAccounts;
    private final ApiUserSteps apiUserSteps;

    public AccountFixture() {
        this(new AccountApiClient(), new OwnedAccounts());
    }

    public AccountFixture(AccountApiClient client, OwnedAccounts accounts) {
        accountApiClient = client;
        ownedAccounts = accounts;
        apiUserSteps = new ApiUserSteps(client, accounts);
    }

    public User newUser() {
        return ownedAccounts.newUser();
    }

    public User createUser() {
        return apiUserSteps.createUser();
    }

    public void cleanup(ITestResult result) {
        try {
            ownedAccounts.cleanup(accountApiClient);
        } catch (AssertionError failure) {
            StringWriter details = new StringWriter();
            failure.printStackTrace(new PrintWriter(details));
            Allure.addAttachment("Account cleanup failure", "text/plain", details.toString());
            if (result.getThrowable() != null) {
                result.getThrowable().addSuppressed(failure);
            } else {
                throw failure;
            }
        }
    }
}
