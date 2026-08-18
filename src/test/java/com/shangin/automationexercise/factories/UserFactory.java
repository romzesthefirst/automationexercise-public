package com.shangin.automationexercise.factories;

import java.util.UUID;

import net.datafaker.Faker;
import com.shangin.automationexercise.model.User;
import net.datafaker.providers.base.Text;

public class UserFactory {
	
	private static final Faker FAKER = new Faker();
	
    private UserFactory() {
        // Utility class
    }

	
	public static User randomUser() {
		String password = FAKER.text().text(
				Text.TextSymbolsBuilder.builder()
						.len(12)
						.with(Text.EN_LOWERCASE, 1)
						.with(Text.EN_UPPERCASE, 1)
						.with(Text.DIGITS, 1)
						.build()
		);

		return new User (
				"Mr",
				FAKER.name().firstName(),
				FAKER.name().lastName(),
				UUID.randomUUID() + "@test.com",
				password,
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

    public static User updatedUserFrom(User currentUser) {
        return new User (
                "Mr",
                FAKER.name().firstName(),
                FAKER.name().lastName(),
                currentUser.email(),
                currentUser.password(),
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
