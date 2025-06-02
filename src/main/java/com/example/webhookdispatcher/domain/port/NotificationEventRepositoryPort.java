package com.example.webhookdispatcher.domain.port;

import com.example.webhookdispatcher.domain.model.NotificationEvent;
import java.util.Optional;
import java.util.UUID;

public interface NotificationEventRepositoryPort {

    NotificationEvent save(NotificationEvent event);

    Optional<NotificationEvent> findById(UUID id);
} 