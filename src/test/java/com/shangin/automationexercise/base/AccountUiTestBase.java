package com.shangin.automationexercise.base;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import com.shangin.automationexercise.fixtures.AccountFixture;

/** Opt-in account lifecycle for UI tests that create test accounts. */
public abstract class AccountUiTestBase extends BaseTest {
    protected final AccountFixture accounts;

    protected AccountUiTestBase() {
        this(new AccountFixture());
    }

    protected AccountUiTestBase(AccountFixture accounts) {
        this.accounts = accounts;
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupAccounts(ITestResult result) {
        accounts.cleanup(result);
    }
}
