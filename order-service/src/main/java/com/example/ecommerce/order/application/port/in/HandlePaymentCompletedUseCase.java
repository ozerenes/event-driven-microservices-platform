package com.example.ecommerce.order.application.port.in;

/**
 * Inbound port: handle PaymentCompleted event (mark order PAID).
 * Idempotent: same eventId is a no-op after first processing.
 */
public interface HandlePaymentCompletedUseCase {

    void handle(HandlePaymentCompletedCommand command);

    record HandlePaymentCompletedCommand(
        String eventId,
        String orderId,
        String paymentId,
        String amount,
        String currency,
        java.time.Instant processedAt
    ) {}
}
