package com.example.ecommerce.order.application;

import com.example.ecommerce.order.application.port.in.GetOrderUseCase;
import com.example.ecommerce.order.application.port.out.OrderRepository;
import com.example.ecommerce.order.domain.Order;
import com.example.ecommerce.order.domain.OrderId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class GetOrderService implements GetOrderUseCase {

    private final OrderRepository orderRepository;

    public GetOrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getById(String orderId) {
        return orderRepository.findById(OrderId.of(orderId));
    }
}
