package com.shangin.automationexercise.factories;

import net.datafaker.Faker;
import com.shangin.automationexercise.model.Feedback;

public class FeedbackFactory {
    
    private FeedbackFactory() {
    }
    
    public static Feedback randomFeedback() {
        Faker faker = TestData.faker();
        return new Feedback (
                faker.name().firstName() + " " + faker.name().lastName(),
                faker.internet().emailAddress(),
                faker.lorem().sentence(),
                faker.lorem().paragraph()
                );
    }

}
