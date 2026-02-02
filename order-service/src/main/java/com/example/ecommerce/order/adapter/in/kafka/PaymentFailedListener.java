package com.example.ecommerce.order.adapter.in.kafka;

import com.example.ecommerce.order.application.port.in.HandlePaymentFailedCommand;
import com.example.ecommerce.order.application.port.in.HandlePaymentFailedUseCase;
import com.example.ecommerce.events.payment.PaymentFailedPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Inbound adapter: consumes PaymentFailed from Kafka and delegates to use case (idempotent).
 */
@Component
public class PaymentFailedListener {

    private static final Logger logger = LoggerFactory.getLogger(PaymentFailedListener.class);

    private final HandlePaymentFailedUseCase useCase;
    private final ObjectMapper objectMapper;

    public PaymentFailedListener(HandlePaymentFailedUseCase useCase, ObjectMapper objectMapper) {
        this.useCase = useCase;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
        topics = "${kafka.topics.payment-failed:payment.failed}",
        groupId = "${spring.kafka.consumer.group-id:order-service}"
    )
    public void onPaymentFailed(
        @Payload String message,
        @Header(KafkaHeaders.RECEIVED_KEY) String key
    ) {
        try {
            var tree = objectMapper.readTree(message);
            String eventId = tree.has("eventId") ? tree.get("eventId").asText() : null;
            var payload = objectMapper.treeToValue(tree.get("payload"), PaymentFailedPayload.class);
            if (payload == null) {
                throw new IllegalArgumentException("Missing payload");
            }
            HandlePaymentFailedCommand command = new HandlePaymentFailedCommand(
                eventId,
                payload.orderId(),
                payload.paymentId(),
                payload.reason(),
                payload.failedAt()
            );
            useCase.handle(command);
        } catch (Exception e) {
            logger.error("Failed to process PaymentFailed key={}", key, e);
            throw new RuntimeException(e); // trigger retry
        }
    }
}
