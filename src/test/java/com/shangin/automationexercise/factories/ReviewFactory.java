package com.shangin.automationexercise.factories;

import net.datafaker.Faker;
import com.shangin.automationexercise.model.Review;

public class ReviewFactory {

    private ReviewFactory() {
    }
    
    public static Review randomReview() {
        Faker faker = TestData.faker();
        return new Review (
                faker.name().firstName() + " " + faker.name().lastName(),
                faker.internet().emailAddress(),
                faker.lorem().paragraph()
                );
    }
}
