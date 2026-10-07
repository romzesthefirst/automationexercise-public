package com.shangin.automationexercise.tests.support;

import com.shangin.automationexercise.api.support.OwnedAccounts;
import com.shangin.automationexercise.base.AccountUiTestBase;
import com.shangin.automationexercise.fixtures.AccountFixture;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/** Exercises the UI account lifecycle without starting a browser. */
public class UiAccountFailureFixture extends AccountUiTestBase {
    private final boolean setupFailure;

    public UiAccountFailureFixture() {
        this(new AccountCleanupTest.FakeClient(), false);
    }

    public UiAccountFailureFixture(AccountCleanupTest.FakeClient client, boolean setupFailure) {
        super(new AccountFixture(client, new OwnedAccounts()));
        this.setupFailure = setupFailure;
    }

    @Override
    @BeforeMethod(alwaysRun = true)
    public void setup() {
        accounts.createUser();
        if (setupFailure) {
            throw new IllegalStateException("Original UI setup failure");
        }
    }

    @Override
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        // No browser was created by this local fixture.
    }

    @Test
    public void failsAfterRegistration() {
        throw new AssertionError("Original UI test failure");
    }
}
