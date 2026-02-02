package com.example.ecommerce.events.order;

import com.example.ecommerce.events.Money;

import java.util.List;

public record OrderCreatedPayload(
    String orderId,
    String customerId,
    Money amount,
    List<OrderItem> items,
    String status
) {}
