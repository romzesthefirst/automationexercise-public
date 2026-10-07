package com.shangin.automationexercise.api.support;

import com.shangin.automationexercise.api.clients.AccountApiClient;
import com.shangin.automationexercise.factories.UserFactory;
import com.shangin.automationexercise.model.User;
import java.util.ArrayList;
import java.util.List;

/** Only fresh, generated identities can enter the cleanup registry. */
public class OwnedAccounts {
    private final ThreadLocal<List<User>> accounts = ThreadLocal.withInitial(ArrayList::new);

    public User newUser() {
        User user = UserFactory.randomUser();
        accounts.get().add(user);
        return user;
    }

    public void cleanup(AccountApiClient client) {
        AssertionError failures = new AssertionError("Test account cleanup failed");
        try {
            for (User user : accounts.get()) {
                try {
                    ApiCleanupHelper.deleteAccount(client, user);
                } catch (Exception | AssertionError failure) {
                    failures.addSuppressed(
                            new AssertionError("Cleanup failed for " + user.email(), failure));
                }
            }
        } finally {
            accounts.remove();
        }
        if (failures.getSuppressed().length > 0) {
            throw failures;
        }
    }
}
