package com.example.webhookdispatcher.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NotificationEventTest {

    private NotificationEvent notificationEvent;
    private ObjectMapper objectMapper;
    private JsonNode testPayload;

    @BeforeEach
    void setUp() throws Exception {
        objectMapper = new ObjectMapper();
        testPayload = objectMapper.readTree("{\"test\":\"data\"}");
        notificationEvent = NotificationEvent.builder()
                .id(UUID.randomUUID())
                .eventType("TEST_EVENT")
                .payload(testPayload)
                .client(new Client())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void whenIncrementAttempts_thenAttemptsIncrease() {
        int initialAttempts = notificationEvent.getAttempts();
        notificationEvent.incrementAttempts();
        assertEquals(initialAttempts + 1, notificationEvent.getAttempts());
    }

    @Test
    void whenMaxAttemptsReached_thenStatusChangesToFailed() {
        for (int i = 0; i <= NotificationEvent.MAX_ATTEMPTS; i++) {
            notificationEvent.incrementAttempts();
        }
        assertEquals(NotificationEvent.NotificationStatus.FAILED, notificationEvent.getStatus());
        assertEquals("Maximum number of attempts reached", notificationEvent.getErrorMessage());
    }

    @Test
    void whenMarkAsSuccess_thenStatusChangesToSuccess() {
        notificationEvent.markAsSuccess();
        assertEquals(NotificationEvent.NotificationStatus.SUCCESS, notificationEvent.getStatus());
        assertNotNull(notificationEvent.getUpdatedAt());
    }

    @Test
    void whenMarkAsFailed_thenStatusChangesToFailed() {
        notificationEvent.markAsFailed();
        assertEquals(NotificationEvent.NotificationStatus.FAILED, notificationEvent.getStatus());
        assertNotNull(notificationEvent.getUpdatedAt());
    }

    @Test
    void whenUpdatePayload_thenPayloadIsUpdated() throws Exception {
        JsonNode newPayload = objectMapper.readTree("{\"new\":\"data\"}");
        notificationEvent.updatePayload(newPayload);
        assertEquals(newPayload, notificationEvent.getPayload());
        assertNotNull(notificationEvent.getUpdatedAt());
    }
} 