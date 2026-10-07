package com.shangin.automationexercise.factories;

import com.shangin.automationexercise.model.CardDetails;
import net.datafaker.Faker;

public class CardFactory {

    private CardFactory() {
        // Utility class
    }

    public static CardDetails randomCard() {
        Faker faker = TestData.faker();
        return new CardDetails(
                faker.name().fullName(),
                faker.finance().creditCard(),
                String.valueOf(faker.number().numberBetween(100, 1000)),
                String.format("%02d", faker.number().numberBetween(1, 13)),
                String.valueOf(faker.number().numberBetween(2027, 2035)));
    }
}
