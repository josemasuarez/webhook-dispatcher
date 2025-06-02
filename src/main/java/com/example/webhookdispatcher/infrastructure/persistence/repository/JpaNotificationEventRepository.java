package com.example.webhookdispatcher.infrastructure.persistence.repository;

import com.example.webhookdispatcher.domain.model.NotificationEvent;
import com.example.webhookdispatcher.domain.repository.NotificationEventRepository;
import com.example.webhookdispatcher.infrastructure.persistence.entity.NotificationEventEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaNotificationEventRepository implements NotificationEventRepository {

    private final SpringDataNotificationEventRepository repository;

    @Override
    public NotificationEvent save(NotificationEvent event) {
        NotificationEventEntity entity = mapToEntity(event);
        NotificationEventEntity savedEntity = repository.save(entity);
        return mapToDomain(savedEntity);
    }

    @Override
    public NotificationEvent findById(UUID id) {
        return repository.findById(id)
                .map(this::mapToDomain)
                .orElseThrow(() -> new RuntimeException("Notification event not found: " + id));
    }

    private NotificationEventEntity mapToEntity(NotificationEvent event) {
        if (event == null) {
            return null;
        }

        NotificationEventEntity.NotificationEventEntityBuilder builder = NotificationEventEntity.builder()
                .id(event.getId())
                .eventType(event.getEventType())
                .payload(event.getPayload())
                .status(event.getStatus())
                .attempts(event.getAttempts())
                .errorMessage(event.getErrorMessage())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt());

        if (event.getClient() != null) {
            builder.clientId(event.getClient().getId());
        }

        return builder.build();
    }

    private NotificationEvent mapToDomain(NotificationEventEntity entity) {
        if (entity == null) {
            return null;
        }

        NotificationEvent.NotificationEventBuilder builder = NotificationEvent.builder()
                .id(entity.getId())
                .eventType(entity.getEventType())
                .payload(entity.getPayload())
                .status(entity.getStatus())
                .attempts(entity.getAttempts())
                .errorMessage(entity.getErrorMessage())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt());

        if (entity.getClient() != null) {
            builder.client(com.example.webhookdispatcher.domain.model.Client.builder()
                    .id(entity.getClient().getId())
                    .name(entity.getClient().getName())
                    .webhookUrl(entity.getClient().getWebhookUrl())
                    .createdAt(entity.getClient().getCreatedAt())
                    .updatedAt(entity.getClient().getUpdatedAt())
                    .build());
        } else if (entity.getClientId() != null) {
            builder.client(com.example.webhookdispatcher.domain.model.Client.builder()
                    .id(entity.getClientId())
                    .build());
        }

        return builder.build();
    }
} 