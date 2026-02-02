package com.example.ecommerce.events;

import java.time.Instant;

/**
 * Shared envelope for all domain events. Used for idempotency (eventId) and tracing (correlationId).
 * Services deserialize payload to the concrete payload type (OrderCreatedPayload, etc.).
 */
public record EventEnvelope<T>(
    String eventId,
    String eventType,
    String aggregateId,
    String aggregateType,
    Instant timestamp,
    String version,
    String correlationId,
    String causationId,
    T payload
) {}
