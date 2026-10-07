package com.shangin.automationexercise.factories;

import com.shangin.automationexercise.support.TestRandom;
import java.util.Locale;
import net.datafaker.Faker;

public final class TestData {
    private TestData() {}

    public static Faker faker() {
        return new Faker(Locale.ENGLISH, TestRandom.data());
    }
}
