package com.example.ecommerce.order.application;

import com.example.ecommerce.order.application.port.in.HandlePaymentFailedCommand;
import com.example.ecommerce.order.application.port.in.HandlePaymentFailedUseCase;
import com.example.ecommerce.order.application.port.out.AppendToOutboxPort;
import com.example.ecommerce.order.application.port.out.OrderRepository;
import com.example.ecommerce.order.application.port.out.ProcessedEventStore;
import com.example.ecommerce.order.domain.OrderId;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Application use case: handle PaymentFailed (cancel order, publish OrderCancelled via outbox). Idempotent by eventId.
 */
@Service
public class HandlePaymentFailedService implements HandlePaymentFailedUseCase {

    private static final String TOPIC_ORDER_CANCELLED = "order.cancelled";
    private static final String EVENT_TYPE_ORDER_CANCELLED = "OrderCancelled";

    private final OrderRepository orderRepository;
    private final AppendToOutboxPort outbox;
    private final ProcessedEventStore processedEventStore;
    private final ObjectMapper objectMapper;

    public HandlePaymentFailedService(OrderRepository orderRepository, AppendToOutboxPort outbox,
                                      ProcessedEventStore processedEventStore, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.outbox = outbox;
        this.processedEventStore = processedEventStore;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void handle(HandlePaymentFailedCommand command) {
        if (processedEventStore.alreadyProcessed(command.eventId())) {
            return;
        }
        orderRepository.findById(OrderId.of(command.orderId()))
            .ifPresent(order -> {
                order.cancel(command.reason());
                orderRepository.save(order);
                String payloadJson = toOrderCancelledPayload(command.orderId(), command.reason());
                outbox.append(new AppendToOutboxPort.OutboxEntry(
                    "Order",
                    command.orderId(),
                    EVENT_TYPE_ORDER_CANCELLED,
                    payloadJson,
                    TOPIC_ORDER_CANCELLED
                ));
                processedEventStore.markProcessed(command.eventId());
            });
    }

    private String toOrderCancelledPayload(String orderId, String reason) {
        var payload = new OrderCancelledPayloadDto(orderId, reason, Instant.now().toString());
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize OrderCancelled payload", e);
        }
    }

    private record OrderCancelledPayloadDto(String orderId, String reason, String cancelledAt) {}
}
