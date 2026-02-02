package com.example.ecommerce.order.adapter.in.rest;

/**
 * REST response DTO: created order id and status.
 */
public record PlaceOrderResponse(String orderId, String status) {}
