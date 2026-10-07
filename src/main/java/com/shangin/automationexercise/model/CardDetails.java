package com.shangin.automationexercise.model;

public record CardDetails(
        String nameOnCard,
        String number,
        String cvc,
        String expirationMonth,
        String expirationYear) {}
