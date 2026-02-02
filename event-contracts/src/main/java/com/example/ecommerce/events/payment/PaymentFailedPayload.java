package com.example.ecommerce.events.payment;

import java.time.Instant;

public record PaymentFailedPayload(
    String paymentId,
    String orderId,
    String reason,
    String status,
    Instant failedAt
) {}
