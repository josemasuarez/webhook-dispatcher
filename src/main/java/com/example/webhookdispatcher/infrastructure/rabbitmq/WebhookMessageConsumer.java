package com.example.webhookdispatcher.infrastructure.rabbitmq;

import com.example.webhookdispatcher.domain.model.NotificationEvent;
import com.example.webhookdispatcher.domain.port.NotificationEventServicePort;
import com.rabbitmq.client.Channel;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookMessageConsumer {

    private final NotificationEventServicePort notificationEventServicePort;

    @RabbitListener(queues = "${rabbitmq.queue.webhook-delivery}")
    public void consumeWebhookMessage(
            @Payload WebhookMessage message,
            @Header(AmqpHeaders.CHANNEL) Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long tag) {

        UUID eventId = message.getId();
        NotificationEvent event = notificationEventServicePort.findById(eventId);

        try {
            notificationEventServicePort.incrementAttempts(event.getId());
            notificationEventServicePort.deliverWebhook(event);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("Error delivering webhook for eventId: {}. Error: {}", eventId, e.getMessage(), e);
            throw new RuntimeException("Error delivering webhook", e);
        }
    }
}
