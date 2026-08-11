package com.shangin.automationexercise.api.models;


public record ProductDto(
        int id,
        String name,
        String price,
        String brand,
        CategoryDto category
        )
{}