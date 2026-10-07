package com.shangin.automationexercise.tests.support;

import com.shangin.automationexercise.api.clients.AccountApiClient;
import com.shangin.automationexercise.api.support.OwnedAccounts;
import com.shangin.automationexercise.base.AccountTestBase;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AccountFailureFixture extends AccountTestBase {
    private final boolean setupFailure;
    private final boolean testFailure;

    public AccountFailureFixture() {
        this(new AccountCleanupTest.FakeClient(), false);
    }

    public AccountFailureFixture(AccountApiClient client, boolean setupFailure) {
        this(client, setupFailure, true);
    }

    public AccountFailureFixture(
            AccountApiClient client, boolean setupFailure, boolean testFailure) {
        super(client, new OwnedAccounts());
        this.setupFailure = setupFailure;
        this.testFailure = testFailure;
    }

    @BeforeMethod
    public void create() {
        accounts.createUser();
        if (setupFailure) {
            throw new IllegalStateException("Original setup failure");
        }
    }

    @Test
    public void exerciseAccountLifecycle() {
        if (testFailure) {
            throw new AssertionError("Original test failure");
        }
    }
}
