package com.example.webhookdispatcher.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    public static final int MAX_ATTEMPTS = 3;
    
    private UUID id;
    @Builder.Default
    private Integer attempts = 0;
    private String eventType;
    private JsonNode payload;
    @Builder.Default
    private NotificationStatus status = NotificationStatus.PENDING;
    private Client client;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String errorMessage;

    public void incrementAttempts() {
        if (this.attempts == null) {
            this.attempts = 0;
        }
        
        if (this.attempts >= MAX_ATTEMPTS) {
            this.status = NotificationStatus.FAILED;
            this.errorMessage = "Maximum number of attempts reached";
        } else {
            this.attempts++;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsSuccess() {
        this.status = NotificationStatus.SUCCESS;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsFailed() {
        this.status = NotificationStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePayload(JsonNode newPayload) {
        this.payload = newPayload;
        this.updatedAt = LocalDateTime.now();
    }

    public enum NotificationStatus {
        PENDING,
        SUCCESS,
        FAILED
    }
} 