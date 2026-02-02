package com.example.ecommerce.order.application;

import com.example.ecommerce.order.application.port.in.PlaceOrderCommand;
import com.example.ecommerce.order.application.port.in.PlaceOrderUseCase;
import com.example.ecommerce.order.application.port.out.AppendToOutboxPort;
import com.example.ecommerce.order.application.port.out.OrderRepository;
import com.example.ecommerce.order.domain.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Application use case: place order and publish OrderCreated via outbox (same transaction).
 */
@Service
public class PlaceOrderService implements PlaceOrderUseCase {

    private static final String TOPIC_ORDER_CREATED = "order.created";
    private static final String EVENT_TYPE_ORDER_CREATED = "OrderCreated";

    private final OrderRepository orderRepository;
    private final AppendToOutboxPort outbox;
    private final ObjectMapper objectMapper;

    public PlaceOrderService(OrderRepository orderRepository, AppendToOutboxPort outbox, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.outbox = outbox;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public OrderId placeOrder(PlaceOrderCommand command) {
        OrderId orderId = OrderId.generate();
        List<OrderItem> items = command.items().stream()
            .map(dto -> new OrderItem(
                dto.productId(),
                dto.quantity(),
                new BigDecimal(dto.unitPrice())
            ))
            .toList();
        Money total = items.stream()
            .map(i -> i.lineTotal(command.currency()))
            .reduce(
                Money.of("0", command.currency()),
                (a, b) -> new Money(a.amount().add(b.amount()), command.currency())
            );
        Order order = new Order(orderId, command.customerId(), items, total);
        orderRepository.save(order);

        String payloadJson = toOrderCreatedPayload(orderId, command, order);
        outbox.append(new AppendToOutboxPort.OutboxEntry(
            "Order",
            orderId.value(),
            EVENT_TYPE_ORDER_CREATED,
            payloadJson,
            TOPIC_ORDER_CREATED
        ));

        return orderId;
    }

    private String toOrderCreatedPayload(OrderId orderId, PlaceOrderCommand command, Order order) throws RuntimeException {
        var payload = new OrderCreatedPayloadDto(
            orderId.value(),
            command.customerId(),
            new MoneyDto(order.getTotalAmount().amountAsString(), order.getTotalAmount().currency()),
            order.getItems().stream()
                .map(i -> new ItemPayload(i.productId(), i.quantity(), i.unitPriceAsString()))
                .toList(),
            order.getStatus().name()
        );
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize OrderCreated payload", e);
        }
    }

    private record OrderCreatedPayloadDto(String orderId, String customerId, MoneyDto amount, List<ItemPayload> items, String status) {}
    private record MoneyDto(String value, String currency) {}
    private record ItemPayload(String productId, int quantity, String unitPrice) {}
}
