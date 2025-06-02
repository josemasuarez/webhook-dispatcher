package com.example.webhookdispatcher.infrastructure.persistence.adapter;

import com.example.webhookdispatcher.domain.model.NotificationEvent;
import com.example.webhookdispatcher.domain.port.NotificationEventRepositoryPort;
import com.example.webhookdispatcher.infrastructure.persistence.repository.JpaNotificationEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationEventRepositoryAdapter implements NotificationEventRepositoryPort {

    private final JpaNotificationEventRepository jpaRepository;

    @Override
    public NotificationEvent save(NotificationEvent event) {
        return jpaRepository.save(event);
    }

    @Override
    public Optional<NotificationEvent> findById(UUID id) {
        return Optional.ofNullable(jpaRepository.findById(id));
    }
} 