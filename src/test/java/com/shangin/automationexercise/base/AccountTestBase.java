package com.shangin.automationexercise.base;

import com.shangin.automationexercise.api.clients.AccountApiClient;
import com.shangin.automationexercise.api.support.OwnedAccounts;
import com.shangin.automationexercise.fixtures.AccountFixture;
import com.shangin.automationexercise.support.TestRandom;
import io.qameta.allure.Allure;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class AccountTestBase {
    protected final AccountApiClient accountApiClient;
    protected final AccountFixture accounts;

    protected AccountTestBase() {
        this(new AccountApiClient(), new OwnedAccounts());
    }

    protected AccountTestBase(AccountApiClient client, OwnedAccounts ownedAccounts) {
        accountApiClient = client;
        accounts = new AccountFixture(client, ownedAccounts);
    }

    @BeforeMethod(alwaysRun = true)
    public void initializeRandomData() {
        TestRandom.begin(
                message -> {
                    System.out.println(message);
                    Allure.addAttachment("Test selection", "text/plain", message);
                });
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupAccounts(ITestResult result) {
        try {
            accounts.cleanup(result);
        } finally {
            TestRandom.clear();
        }
    }
}
