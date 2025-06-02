package com.example.webhookdispatcher.infrastructure.persistence.repository;

import com.example.webhookdispatcher.infrastructure.persistence.entity.NotificationEventEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataNotificationEventRepository extends JpaRepository<NotificationEventEntity, UUID> {

}