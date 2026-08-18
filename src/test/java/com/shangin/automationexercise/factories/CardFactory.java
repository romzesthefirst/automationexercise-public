package com.shangin.automationexercise.factories;

import net.datafaker.Faker;
import com.shangin.automationexercise.model.CardDetails;

public class CardFactory {
    private static final Faker FAKER = new Faker();

    private CardFactory() {
        // Utility class
    }
    
    public static CardDetails randomCard() {
        return new CardDetails (
                FAKER.name().fullName(),
                FAKER.finance().creditCard(),
                String.valueOf(FAKER.number().numberBetween(100, 1000)),
                String.format("%02d", FAKER.number().numberBetween(1, 13)),
                String.valueOf(FAKER.number().numberBetween(2027, 2035))
                );
    }
}