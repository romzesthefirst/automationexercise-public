package com.shangin.automationexercise.factories;

import java.util.Locale;
import net.datafaker.Faker;
import com.shangin.automationexercise.support.TestRandom;

public final class TestData {
    private TestData() { }
    public static Faker faker() {
        return new Faker(Locale.ENGLISH, TestRandom.data());
    }
}
