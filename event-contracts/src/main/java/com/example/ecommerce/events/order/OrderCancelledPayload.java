package com.example.ecommerce.events.order;

import java.time.Instant;

public record OrderCancelledPayload(
    String orderId,
    String reason,
    Instant cancelledAt
) {}
