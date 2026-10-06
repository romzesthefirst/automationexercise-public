package com.shangin.automationexercise.base;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import com.shangin.automationexercise.api.clients.AccountApiClient;
import com.shangin.automationexercise.api.support.OwnedAccounts;
import com.shangin.automationexercise.fixtures.AccountFixture;

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

    @AfterMethod(alwaysRun = true)
    public void cleanupAccounts(ITestResult result) {
        accounts.cleanup(result);
    }
}
