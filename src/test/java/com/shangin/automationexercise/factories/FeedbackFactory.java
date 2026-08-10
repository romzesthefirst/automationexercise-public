package com.shangin.automationexercise.factories;

import com.github.javafaker.Faker;
import com.shangin.automationexercise.model.Feedback;

public class FeedbackFactory {
    
    private static final Faker FAKER = new Faker();
    
    private FeedbackFactory() {
    }
    
    public static Feedback randomFeedback() {
        return new Feedback (
                FAKER.name().firstName() + " " + FAKER.name().lastName(),
                FAKER.internet().emailAddress(),
                FAKER.lorem().sentence(),
                FAKER.lorem().paragraph()
                );
    }

}
