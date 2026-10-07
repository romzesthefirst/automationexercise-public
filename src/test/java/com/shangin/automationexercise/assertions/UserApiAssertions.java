package com.shangin.automationexercise.assertions;

import com.shangin.automationexercise.api.models.UserDetailsDto;
import com.shangin.automationexercise.model.User;
import org.testng.Assert;

public class UserApiAssertions {

    private UserApiAssertions() {}

    public static void assertMatches(User expected, UserDetailsDto actual) {

        Assert.assertEquals(actual.name(), expected.firstName(), "Unexpected user name");

        Assert.assertEquals(actual.email(), expected.email(), "Unexpected user email");

        Assert.assertEquals(actual.title(), expected.title(), "Unexpected user title");

        Assert.assertEquals(
                actual.birth_day(), expected.dayOfBirth(), "Unexpected user day of birth");

        Assert.assertEquals(
                actual.birth_month(), expected.monthOfBirth(), "Unexpected user month of birth");

        Assert.assertEquals(
                actual.birth_year(), expected.yearOfBirth(), "Unexpected user year of birth");

        Assert.assertEquals(
                actual.first_name(), expected.firstName(), "Unexpected user first name");

        Assert.assertEquals(actual.last_name(), expected.lastName(), "Unexpected user last name");

        Assert.assertEquals(actual.company(), expected.company(), "Unexpected user company");

        Assert.assertEquals(
                actual.address1(), expected.addressLine1(), "Unexpected user address line 1");

        Assert.assertEquals(
                actual.address2(), expected.addressLine2(), "Unexpected user address line 1");

        Assert.assertEquals(actual.country(), expected.country(), "Unexpected user country");

        Assert.assertEquals(actual.state(), expected.state(), "Unexpected user state");

        Assert.assertEquals(actual.city(), expected.city(), "Unexpected user city");

        Assert.assertEquals(actual.zipcode(), expected.zipcode(), "Unexpected user zipcode");
    }
}
