package com.shangin.automationexercise.mappers;

import java.util.Map;

import com.shangin.automationexercise.model.User;

public final class UserApiMapper {

    private UserApiMapper() {
    }

    public static Map<String, Object> toCreateAccountForm(User user) {
        return Map.ofEntries(
                Map.entry("name",          user.firstName()),
                Map.entry("email",         user.email()),
                Map.entry("password",      user.password()),
                Map.entry("title",         user.title()),
                Map.entry("birth_date",    user.dayOfBirth()),
                Map.entry("birth_month",   user.monthOfBirth()),
                Map.entry("birth_year",    user.yearOfBirth()),
                Map.entry("firstname",     user.firstName()),
                Map.entry("lastname",      user.lastName()),
                Map.entry("company",       user.company()),
                Map.entry("address1",      user.addressLine1()),
                Map.entry("address2",      user.addressLine2()),
                Map.entry("country",       user.country()),
                Map.entry("zipcode",       user.zipcode()),
                Map.entry("state",         user.state()),
                Map.entry("city",          user.city()),
                Map.entry("mobile_number", user.phone())
        );
    }
}