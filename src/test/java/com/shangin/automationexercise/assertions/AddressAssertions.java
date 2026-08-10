package com.shangin.automationexercise.assertions;

import org.testng.Assert;

import com.shangin.automationexercise.components.AddressComponent;
import com.shangin.automationexercise.formatters.UserAddressFormatter;
import com.shangin.automationexercise.model.User;

public class AddressAssertions {
    private AddressAssertions() {
    }

    public static void assertMatchesUser(AddressComponent actualAddress, User expectedUser) {
        Assert.assertEquals(
                actualAddress.getFullName(),
                UserAddressFormatter.fullName(expectedUser),
                "Full name is incorrect");

        Assert.assertEquals(
                actualAddress.getCompany(),
                expectedUser.company(),
                "Company is incorrect");

        Assert.assertEquals(
                actualAddress.getAddressLine1(),
                expectedUser.addressLine1(),
                "Address line 1 is incorrect");

        Assert.assertEquals(
                actualAddress.getAddressLine2(),
                expectedUser.addressLine2(),
                "Address line 2 is incorrect");

        Assert.assertEquals(
                actualAddress.getCityStatePostcode(),
                UserAddressFormatter.cityStatePostcode(expectedUser),
                "City/state/postcode is incorrect");

        Assert.assertEquals(
                actualAddress.getCountry(),
                expectedUser.country(),
                "Country is incorrect");

        Assert.assertEquals(actualAddress.getPhone(), expectedUser.phone(), "Phone is incorrect");
    }
}
