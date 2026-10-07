package com.shangin.automationexercise.model;

public record User(
        String title,
        String firstName,
        String lastName,
        String email,
        String password,
        String dayOfBirth,
        String monthOfBirth,
        String yearOfBirth,
        String company,
        String addressLine1,
        String addressLine2,
        String country,
        String state,
        String city,
        String zipcode,
        String phone) {}
