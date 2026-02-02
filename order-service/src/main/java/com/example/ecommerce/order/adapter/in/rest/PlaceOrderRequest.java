package com.example.ecommerce.order.adapter.in.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * REST request DTO: place order. No business logic; controller maps to use case command.
 */
public record PlaceOrderRequest(
    @NotBlank String customerId,
    @NotBlank String currency,
    @NotEmpty @Valid List<OrderItemRequest> items
) {
    public record OrderItemRequest(
        @NotBlank String productId,
        @NotNull Integer quantity,
        @NotBlank String unitPrice
    ) {}
}
