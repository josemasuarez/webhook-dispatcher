package com.example.webhookdispatcher.domain.port;

public interface WebhookClientPort {
    void sendWebhook(String webhookUrl, String payload);
} 