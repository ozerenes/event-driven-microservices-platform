package com.example.ecommerce.events.order;

public record OrderItem(String productId, int quantity, String unitPrice) {}
