package com.example.webhookdispatcher.domain.port;

import com.example.webhookdispatcher.domain.model.NotificationEvent;
import java.util.UUID;

public interface NotificationEventServicePort {

    void deliverWebhook(NotificationEvent notificationEvent);

    void incrementAttempts(UUID eventId);

    void markAsSuccess(UUID eventId);

    void markAsFailed(UUID eventId, String errorMessage);

    NotificationEvent findById(UUID id);
}