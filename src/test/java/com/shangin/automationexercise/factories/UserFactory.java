package com.shangin.automationexercise.factories;

import java.util.UUID;

import com.github.javafaker.Faker;
import com.shangin.automationexercise.model.User;

public class UserFactory {
	
	private static final Faker FAKER = new Faker();
	
    private UserFactory() {
        // Utility class
    }
	
	public static User randomUser() {
		return new User (
				"Mr",
				FAKER.name().firstName(),
				FAKER.name().lastName(),
				UUID.randomUUID() + "@test.com",
				FAKER.internet().password(8, 16, true, true),
				String.valueOf(FAKER.number().numberBetween(1, 28)),
                String.valueOf(FAKER.number().numberBetween(1, 12)),
                String.valueOf(FAKER.number().numberBetween(1990, 2005)),
                FAKER.company().name(),
                FAKER.address().streetAddress(),
                FAKER.address().secondaryAddress(),
                "Israel",
                FAKER.address().state(),
                FAKER.address().city(),
                FAKER.address().zipCode(),
                FAKER.phoneNumber().cellPhone()
				);
	}
}
