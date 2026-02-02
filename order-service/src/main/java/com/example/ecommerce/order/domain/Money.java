package com.example.ecommerce.order.domain;

import java.math.BigDecimal;

/**
 * Value object: monetary amount with currency.
 */
public record Money(BigDecimal amount, String currency) {

    public Money {
        if (amount == null || currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("amount and currency must be non-null");
        }
    }

    public static Money of(String value, String currency) {
        return new Money(new BigDecimal(value), currency);
    }

    public String amountAsString() {
        return amount.stripTrailingZeros().toPlainString();
    }
}
