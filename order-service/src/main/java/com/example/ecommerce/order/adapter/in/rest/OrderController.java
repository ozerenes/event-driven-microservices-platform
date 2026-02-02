package com.example.ecommerce.order.adapter.in.rest;

import com.example.ecommerce.order.application.port.in.GetOrderUseCase;
import com.example.ecommerce.order.application.port.in.PlaceOrderCommand;
import com.example.ecommerce.order.application.port.in.PlaceOrderUseCase;
import com.example.ecommerce.order.domain.Order;
import com.example.ecommerce.order.domain.OrderId;
import com.example.ecommerce.order.domain.OrderStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Inbound adapter: REST API. Delegates to use cases; no business logic.
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final PlaceOrderUseCase placeOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;

    public OrderController(PlaceOrderUseCase placeOrderUseCase, GetOrderUseCase getOrderUseCase) {
        this.placeOrderUseCase = placeOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
    }

    @PostMapping
    public ResponseEntity<PlaceOrderResponse> placeOrder(@RequestBody @Valid PlaceOrderRequest request) {
        PlaceOrderCommand command = new PlaceOrderCommand(
            request.customerId(),
            request.currency(),
            request.items().stream()
                .map(i -> new PlaceOrderUseCase.OrderItemDto(i.productId(), i.quantity(), i.unitPrice()))
                .toList()
        );
        OrderId orderId = placeOrderUseCase.placeOrder(command);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new PlaceOrderResponse(orderId.value(), OrderStatus.PENDING.name()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String id) {
        return getOrderUseCase.getById(id)
            .map(OrderController::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    private static OrderResponse toResponse(Order order) {
        return new OrderResponse(
            order.getId().value(),
            order.getCustomerId(),
            order.getStatus().name(),
            order.getTotalAmount().amountAsString(),
            order.getTotalAmount().currency(),
            order.getItems().stream()
                .map(i -> new OrderResponse.OrderItemResponse(i.productId(), i.quantity(), i.unitPriceAsString()))
                .toList()
        );
    }
}
