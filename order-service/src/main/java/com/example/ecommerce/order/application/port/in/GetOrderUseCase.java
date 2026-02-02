package com.example.ecommerce.order.application.port.in;

import com.example.ecommerce.order.domain.Order;

import java.util.Optional;

/**
 * Inbound port: get order by id (query).
 */
public interface GetOrderUseCase {

    Optional<Order> getById(String orderId);
}
