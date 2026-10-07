package com.shangin.automationexercise.factories;

import com.shangin.automationexercise.model.Review;
import net.datafaker.Faker;

public class ReviewFactory {

    private ReviewFactory() {}

    public static Review randomReview() {
        Faker faker = TestData.faker();
        return new Review(
                faker.name().firstName() + " " + faker.name().lastName(),
                faker.internet().emailAddress(),
                faker.lorem().paragraph());
    }
}
