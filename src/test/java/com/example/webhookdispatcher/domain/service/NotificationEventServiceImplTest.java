package com.example.webhookdispatcher.domain.service;

import com.example.webhookdispatcher.domain.model.Client;
import com.example.webhookdispatcher.domain.model.NotificationEvent;
import com.example.webhookdispatcher.domain.port.NotificationEventRepositoryPort;
import com.example.webhookdispatcher.domain.port.WebhookClientPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationEventServiceImplTest {

    @Mock
    private NotificationEventRepositoryPort notificationEventRepository;

    @Mock
    private WebhookClientPort webhookClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private NotificationEventServiceImpl notificationEventService;

    private NotificationEvent notificationEvent;
    private Client client;
    private UUID eventId;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        client = Client.builder()
                .id(UUID.randomUUID())
                .name("Test Client")
                .webhookUrl("https://test.com/webhook")
                .build();

        notificationEvent = NotificationEvent.builder()
                .id(eventId)
                .eventType("TEST_EVENT")
                .client(client)
                .build();
    }

    @Test
    void whenDeliverWebhook_thenSuccess() throws Exception {
        when(notificationEventRepository.findById(eventId)).thenReturn(Optional.of(notificationEvent));
        when(notificationEventRepository.save(any(NotificationEvent.class))).thenReturn(notificationEvent);
        when(objectMapper.valueToTree(any())).thenReturn(null);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        notificationEventService.deliverWebhook(notificationEvent);

        verify(webhookClient).sendWebhook(eq(client.getWebhookUrl()), anyString());
        verify(notificationEventRepository).save(any(NotificationEvent.class));
    }

    @Test
    void whenIncrementAttempts_thenAttemptsIncrease() {
        when(notificationEventRepository.findById(eventId)).thenReturn(Optional.of(notificationEvent));
        when(notificationEventRepository.save(any(NotificationEvent.class))).thenReturn(notificationEvent);

        notificationEventService.incrementAttempts(eventId);

        verify(notificationEventRepository).save(any(NotificationEvent.class));
    }

    @Test
    void whenMarkAsSuccess_thenStatusChangesToSuccess() {
        when(notificationEventRepository.findById(eventId)).thenReturn(Optional.of(notificationEvent));
        when(notificationEventRepository.save(any(NotificationEvent.class))).thenReturn(notificationEvent);

        notificationEventService.markAsSuccess(eventId);

        verify(notificationEventRepository).save(any(NotificationEvent.class));
    }

    @Test
    void whenMarkAsFailed_thenStatusChangesToFailed() {
        when(notificationEventRepository.findById(eventId)).thenReturn(Optional.of(notificationEvent));
        when(notificationEventRepository.save(any(NotificationEvent.class))).thenReturn(notificationEvent);

        notificationEventService.markAsFailed(eventId, "Test error");

        verify(notificationEventRepository).save(any(NotificationEvent.class));
    }

    @Test
    void whenFindById_thenReturnsEvent() {
        when(notificationEventRepository.findById(eventId)).thenReturn(Optional.of(notificationEvent));

        NotificationEvent found = notificationEventService.findById(eventId);

        assertNotNull(found);
        assertEquals(eventId, found.getId());
    }

    @Test
    void whenFindByIdNotFound_thenThrowsException() {
        when(notificationEventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> notificationEventService.findById(eventId));
    }
} 