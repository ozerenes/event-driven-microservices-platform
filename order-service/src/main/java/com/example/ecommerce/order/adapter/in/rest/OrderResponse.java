package com.example.ecommerce.order.adapter.in.rest;

import java.util.List;

/**
 * REST response DTO: order details (for GET).
 */
public record OrderResponse(
    String orderId,
    String customerId,
    String status,
    String totalAmount,
    String currency,
    List<OrderItemResponse> items
) {
    public record OrderItemResponse(String productId, int quantity, String unitPrice) {}
}
