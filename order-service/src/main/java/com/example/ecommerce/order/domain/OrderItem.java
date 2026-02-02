package com.example.ecommerce.order.domain;

import java.math.BigDecimal;

/**
 * Value object: a line item in an order.
 */
public record OrderItem(
    String productId,
    int quantity,
    BigDecimal unitPrice
) {

    public OrderItem {
        if (productId == null || productId.isBlank() || quantity <= 0 || unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid order item");
        }
    }

    public Money lineTotal(String currency) {
        return new Money(unitPrice.multiply(BigDecimal.valueOf(quantity)), currency);
    }

    public String unitPriceAsString() {
        return unitPrice.stripTrailingZeros().toPlainString();
    }
}
