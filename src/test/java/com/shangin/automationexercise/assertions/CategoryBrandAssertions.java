package com.shangin.automationexercise.assertions;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.testng.Assert;

public class CategoryBrandAssertions {

    public static void assertCategoriesMatch(
            Map<String, List<String>> actual,
            Map<String, List<String>> expected) {

        Assert.assertEquals(
                normalizeCategories(actual),
                normalizeCategories(expected),
                "Categories and subcategories do not match");

    }

    private static Map<String, List<String>> normalizeCategories(
            Map<String, List<String>> categories) {

        return categories.entrySet().stream().collect(
                Collectors.toMap(
                        entry -> normalize(entry.getKey()),
                        entry -> entry.getValue().stream().map(CategoryBrandAssertions::normalize)
                                .toList()));
    }

    private static String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

}
