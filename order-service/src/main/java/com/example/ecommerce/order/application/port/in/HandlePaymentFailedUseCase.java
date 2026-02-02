package com.example.ecommerce.order.application.port.in;

/**
 * Inbound port: handle PaymentFailed event (cancel order, publish OrderCancelled via outbox).
 * Idempotent: same eventId is a no-op after first processing.
 */
public interface HandlePaymentFailedUseCase {

    void handle(HandlePaymentFailedCommand command);

    record HandlePaymentFailedCommand(
        String eventId,
        String orderId,
        String paymentId,
        String reason,
        java.time.Instant failedAt
    ) {}
}
