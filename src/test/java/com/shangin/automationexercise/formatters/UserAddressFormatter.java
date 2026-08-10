package com.shangin.automationexercise.formatters;

import com.shangin.automationexercise.model.User;

public final class UserAddressFormatter {

    private UserAddressFormatter() {
    }

    public static String fullName(User user) {
        return "%s. %s %s".formatted(user.title(), user.firstName(), user.lastName());
    }

    public static String cityStatePostcode(User user) {
        return "%s %s %s".formatted(user.city(), user.state(), user.zipcode());
    }
}
