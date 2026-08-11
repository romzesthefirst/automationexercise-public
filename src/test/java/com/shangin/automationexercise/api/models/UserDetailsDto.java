package com.shangin.automationexercise.api.models;

public record UserDetailsDto (
        int id,
        String name,
        String email,
        String title,
        String birth_day,
        String birth_month,
        String birth_year,
        String first_name,
        String last_name,
        String company,
        String address1,
        String address2,
        String country,
        String state,
        String city,
        String zipcode
) {}
