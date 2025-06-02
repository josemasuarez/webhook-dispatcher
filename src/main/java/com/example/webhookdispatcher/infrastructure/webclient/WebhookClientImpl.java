package com.example.webhookdispatcher.infrastructure.webclient;

import com.example.webhookdispatcher.domain.port.WebhookClientPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookClientImpl implements WebhookClientPort {

    private final WebClient webClient;

    @Override
    public void sendWebhook(String webhookUrl, String payload) {
        log.info("Sending webhook to URL: {}", webhookUrl);
        
        webClient.post()
                .uri(webhookUrl)
                .bodyValue(payload)
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(response -> log.info("Webhook sent successfully to: {}", webhookUrl))
                .doOnError(error -> log.error("Error sending webhook to {}: {}", webhookUrl, error.getMessage()))
                .block();
    }
} 