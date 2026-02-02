package com.example.ecommerce.events.notification;

import java.time.Instant;

public record NotificationSentPayload(
    String notificationId,
    String orderId,
    String channel,
    String type,
    Instant sentAt
) {}
