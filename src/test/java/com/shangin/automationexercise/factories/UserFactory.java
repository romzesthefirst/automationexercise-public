package com.shangin.automationexercise.factories;

import java.util.UUID;

import net.datafaker.Faker;
import com.shangin.automationexercise.model.User;
import net.datafaker.providers.base.Text;

public class UserFactory {
	
    private UserFactory() {
        // Utility class
    }

	
	public static User randomUser() {
        Faker faker = TestData.faker();
		String password = faker.text().text(
				Text.TextSymbolsBuilder.builder()
						.len(12)
						.with(Text.EN_LOWERCASE, 1)
						.with(Text.EN_UPPERCASE, 1)
						.with(Text.DIGITS, 1)
						.build()
		);

		return new User (
				"Mr",
				faker.name().firstName(),
				faker.name().lastName(),
				UUID.randomUUID() + "@test.com",
				password,
				String.valueOf(faker.number().numberBetween(1, 28)),
                String.valueOf(faker.number().numberBetween(1, 12)),
                String.valueOf(faker.number().numberBetween(1990, 2005)),
                faker.company().name(),
                faker.address().streetAddress(),
                faker.address().secondaryAddress(),
                "Israel",
                faker.address().state(),
                faker.address().city(),
                faker.address().zipCode(),
                faker.phoneNumber().cellPhone()
				);
	}

    public static User updatedUserFrom(User currentUser) {
        Faker faker = TestData.faker();
        return new User (
                "Mr",
                faker.name().firstName(),
                faker.name().lastName(),
                currentUser.email(),
                currentUser.password(),
                String.valueOf(faker.number().numberBetween(1, 28)),
                String.valueOf(faker.number().numberBetween(1, 12)),
                String.valueOf(faker.number().numberBetween(1990, 2005)),
                faker.company().name(),
                faker.address().streetAddress(),
                faker.address().secondaryAddress(),
                "Israel",
                faker.address().state(),
                faker.address().city(),
                faker.address().zipCode(),
                faker.phoneNumber().cellPhone()
                );
    }
}
