package com.example.webhookdispatcher.infrastructure.rabbitmq;

import com.example.webhookdispatcher.domain.model.Client;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookMessage {
    private UUID id;
    private Client client;
    private String eventType;
    private Map<String, Object> payload;
    private String status;
    private Integer attempts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 