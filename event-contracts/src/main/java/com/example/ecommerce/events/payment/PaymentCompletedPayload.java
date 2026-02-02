package com.example.ecommerce.events.payment;

import com.example.ecommerce.events.Money;

import java.time.Instant;

public record PaymentCompletedPayload(
    String paymentId,
    String orderId,
    Money amount,
    String status,
    Instant processedAt
) {}
