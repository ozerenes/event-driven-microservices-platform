package com.example.ecommerce.order.domain;

import java.util.UUID;

/**
 * Value object: unique identity for an Order aggregate.
 */
public record OrderId(String value) {

    public OrderId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("OrderId must not be blank");
        }
    }

    public static OrderId generate() {
        return new OrderId(UUID.randomUUID().toString());
    }

    public static OrderId of(String value) {
        return new OrderId(value);
    }
}
