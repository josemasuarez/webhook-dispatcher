package com.example.webhookdispatcher.domain.repository;

import com.example.webhookdispatcher.domain.model.NotificationEvent;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationEventRepository {

    NotificationEvent save(NotificationEvent event);

    NotificationEvent findById(UUID id);
}