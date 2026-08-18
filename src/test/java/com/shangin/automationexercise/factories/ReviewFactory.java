package com.shangin.automationexercise.factories;

import net.datafaker.Faker;
import com.shangin.automationexercise.model.Review;

public class ReviewFactory {
    private static final Faker FAKER = new Faker();

    private ReviewFactory() {
    }
    
    public static Review randomReview() {
        return new Review (
                FAKER.name().firstName() + " " + FAKER.name().lastName(),
                FAKER.internet().emailAddress(),
                FAKER.lorem().paragraph()
                );
    }
}
