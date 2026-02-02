package com.example.ecommerce.order.application;

import com.example.ecommerce.order.application.port.in.HandlePaymentCompletedCommand;
import com.example.ecommerce.order.application.port.in.HandlePaymentCompletedUseCase;
import com.example.ecommerce.order.application.port.out.OrderRepository;
import com.example.ecommerce.order.application.port.out.ProcessedEventStore;
import com.example.ecommerce.order.domain.OrderId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application use case: handle PaymentCompleted (mark order PAID). Idempotent by eventId.
 */
@Service
public class HandlePaymentCompletedService implements HandlePaymentCompletedUseCase {

    private final OrderRepository orderRepository;
    private final ProcessedEventStore processedEventStore;

    public HandlePaymentCompletedService(OrderRepository orderRepository, ProcessedEventStore processedEventStore) {
        this.orderRepository = orderRepository;
        this.processedEventStore = processedEventStore;
    }

    @Override
    @Transactional
    public void handle(HandlePaymentCompletedCommand command) {
        if (processedEventStore.alreadyProcessed(command.eventId())) {
            return;
        }
        orderRepository.findById(OrderId.of(command.orderId()))
            .ifPresent(order -> {
                order.markPaid();
                orderRepository.save(order);
                processedEventStore.markProcessed(command.eventId());
            });
    }
}
