package com.example.ecommerce.order.adapter.in.kafka;

import com.example.ecommerce.order.application.port.in.HandlePaymentCompletedCommand;
import com.example.ecommerce.order.application.port.in.HandlePaymentCompletedUseCase;
import com.example.ecommerce.events.payment.PaymentCompletedPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Inbound adapter: consumes PaymentCompleted from Kafka and delegates to use case (idempotent).
 */
@Component
public class PaymentCompletedListener {

    private static final Logger logger = LoggerFactory.getLogger(PaymentCompletedListener.class);

    private final HandlePaymentCompletedUseCase useCase;
    private final ObjectMapper objectMapper;

    public PaymentCompletedListener(HandlePaymentCompletedUseCase useCase, ObjectMapper objectMapper) {
        this.useCase = useCase;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
        topics = "${kafka.topics.payment-completed:payment.completed}",
        groupId = "${spring.kafka.consumer.group-id:order-service}"
    )
    public void onPaymentCompleted(
        @Payload String message,
        @Header(KafkaHeaders.RECEIVED_KEY) String key
    ) {
        try {
            var tree = objectMapper.readTree(message);
            String eventId = tree.has("eventId") ? tree.get("eventId").asText() : null;
            var payload = objectMapper.treeToValue(tree.get("payload"), PaymentCompletedPayload.class);
            if (payload == null) {
                throw new IllegalArgumentException("Missing payload");
            }
            String amountStr = payload.amount() != null ? payload.amount().value() : null;
            String currency = payload.amount() != null ? payload.amount().currency() : null;
            HandlePaymentCompletedCommand command = new HandlePaymentCompletedCommand(
                eventId,
                payload.orderId(),
                payload.paymentId(),
                amountStr,
                currency,
                payload.processedAt()
            );
            useCase.handle(command);
        } catch (Exception e) {
            logger.error("Failed to process PaymentCompleted key={}", key, e);
            throw new RuntimeException(e); // trigger retry
        }
    }
}
