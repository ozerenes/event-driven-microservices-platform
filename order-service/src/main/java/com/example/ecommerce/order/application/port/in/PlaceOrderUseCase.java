package com.example.ecommerce.order.application.port.in;

import com.example.ecommerce.order.domain.Order;
import com.example.ecommerce.order.domain.OrderId;

/**
 * Inbound port: place a new order (primary use case).
 */
public interface PlaceOrderUseCase {

    /**
     * Creates an order and publishes OrderCreated via outbox (same transaction).
     *
     * @param command place order command
     * @return created order id
     */
    OrderId placeOrder(PlaceOrderCommand command);

    record PlaceOrderCommand(
        String customerId,
        String currency,
        java.util.List<OrderItemDto> items
    ) {}

    record OrderItemDto(String productId, int quantity, String unitPrice) {}

    record PlaceOrderResult(OrderId orderId, Order order) {}
}
