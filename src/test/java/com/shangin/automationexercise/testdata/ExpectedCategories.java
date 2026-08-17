package com.shangin.automationexercise.testdata;

import java.util.List;
import java.util.Map;

public class ExpectedCategories {

    private ExpectedCategories() {
    }

    public static final Map<String,
            List<String>> ALL = Map.of(
                    "Women",
                    List.of("Dress", "Tops", "Saree"),
                    "Men",
                    List.of("Tshirts", "Jeans"),
                    "Kids",
                    List.of("Dress", "Tops & Shirts"));

}
