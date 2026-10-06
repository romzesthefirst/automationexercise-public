package com.shangin.automationexercise.model;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Parses nonnegative rupee amounts with a decimal point and optional thousands commas. */
public final class MonetaryValues {
    private static final Pattern PRICE = Pattern.compile(
            "(?:Rs\\.\\s*)?([0-9]+|[0-9]{1,3}(?:,[0-9]{3})+)(\\.[0-9]+)?");

    private MonetaryValues() {
    }

    public static BigDecimal parse(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Invalid monetary value: null");
        }
        Matcher matcher = PRICE.matcher(text.strip());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid monetary value: '" + text + "'");
        }
        return new BigDecimal(matcher.group(1).replace(",", "")
                + (matcher.group(2) == null ? "" : matcher.group(2)));
    }

    /** Returns plain decimal text without a currency prefix or insignificant trailing zeros. */
    public static String format(BigDecimal amount) {
        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException("Invalid monetary amount: " + amount);
        }
        return amount.stripTrailingZeros().toPlainString();
    }
}
