package com.example.webhookdispatcher.domain.service;

import com.example.webhookdispatcher.domain.model.NotificationEvent;
import com.example.webhookdispatcher.domain.port.NotificationEventRepositoryPort;
import com.example.webhookdispatcher.domain.port.NotificationEventServicePort;
import com.example.webhookdispatcher.domain.port.WebhookClientPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEventServiceImpl implements NotificationEventServicePort {

    private final NotificationEventRepositoryPort notificationEventRepository;
    private final WebhookClientPort webhookClient;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void deliverWebhook(NotificationEvent notificationEvent) {
        log.info("Delivering notification event {}", notificationEvent);

        try {
            notificationEvent.updatePayload(objectMapper.valueToTree(notificationEvent.getPayload()));
            String payloadJson = objectMapper.writeValueAsString(notificationEvent.getPayload());

            webhookClient.sendWebhook(notificationEvent.getClient().getWebhookUrl(), payloadJson);

            markAsSuccess(notificationEvent.getId());
            log.info("Webhook delivered successfully for event: {}", notificationEvent.getId());
        } catch (Exception e) {
            log.error("Error sending webhook for clientId: {}, eventType: {}, error: {}",
                    notificationEvent.getClient().getId(), notificationEvent.getEventType(), e.getMessage());
            markAsFailed(notificationEvent.getId(), e.getMessage());
            throw new RuntimeException("Failed to send webhook", e);
        }
    }

    @Override
    @Transactional
    public void incrementAttempts(UUID eventId) {
        NotificationEvent event = findById(eventId);
        if (event != null) {
            log.info("Current attempts before increment: {}", event.getAttempts());
            event.incrementAttempts();
            event = notificationEventRepository.save(event);
            log.info("Incremented attempts for event: {}, new attempts: {}", eventId, event.getAttempts());

            if (event.getAttempts() >= NotificationEvent.MAX_ATTEMPTS) {
                log.info("Maximum attempts reached for event: {}, marking as FAILED", eventId);
                markAsFailed(eventId, "Maximum number of attempts reached");
            }
        } else {
            log.error("Event not found for id: {}", eventId);
        }
    }

    @Override
    @Transactional()
    public void markAsSuccess(UUID eventId) {
        NotificationEvent event = findById(eventId);
        event.markAsSuccess();
        event = notificationEventRepository.save(event);
        log.info("Marked event as success: {}, attempts: {}", eventId, event.getAttempts());
    }

    @Override
    @Transactional
    public void markAsFailed(UUID eventId, String errorMessage) {
        NotificationEvent event = findById(eventId);
        event.markAsFailed();
        event.setErrorMessage(errorMessage);
        event = notificationEventRepository.save(event);
        log.info("Marked event as failed: {}, attempts: {}, error: {}", eventId, event.getAttempts(), errorMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationEvent findById(UUID id) {
        return notificationEventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification event not found"));
    }
} 