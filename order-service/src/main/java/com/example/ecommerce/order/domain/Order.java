package com.example.ecommerce.order.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Domain entity: Order aggregate root.
 * Encapsulates business rules (status transitions, cancellation).
 */
public class Order {

    private final OrderId id;
    private final String customerId;
    private final List<OrderItem> items;
    private final Money totalAmount;
    private OrderStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    public Order(OrderId id, String customerId, List<OrderItem> items, Money totalAmount) {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("customerId must not be blank");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("items must not be empty");
        }
        this.id = id;
        this.customerId = customerId;
        this.items = new ArrayList<>(items);
        this.totalAmount = totalAmount;
        this.status = OrderStatus.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    /** Reconstitute from persistence (all fields). */
    public Order(OrderId id, String customerId, List<OrderItem> items, Money totalAmount,
                 OrderStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.items = new ArrayList<>(items);
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void markPaid() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING order can be marked PAID; current: " + status);
        }
        this.status = OrderStatus.PAID;
        this.updatedAt = Instant.now();
    }

    public void cancel(String reason) {
        if (this.status == OrderStatus.CANCELLED) {
            return; // idempotent
        }
        if (this.status == OrderStatus.PAID) {
            throw new IllegalStateException("Cannot cancel PAID order");
        }
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    public OrderId getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
