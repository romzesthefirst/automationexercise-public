package com.shangin.automationexercise.api.support;

import com.shangin.automationexercise.api.clients.AccountApiClient;
import com.shangin.automationexercise.model.User;

public final class ApiCleanupHelper {

    private ApiCleanupHelper() {
    }

    public static void deleteAccountQuietly(AccountApiClient accountApiClient, User user) {
        try {
            accountApiClient.deleteAccount(user);
        } catch (Exception e) {
            System.err.println("Failed to cleanup user: " + user.email());
        }
    }
}
