package com.example.webhookdispatcher.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    @Test
    void whenCreateClient_thenFieldsAreSetCorrectly() {
        String name = "Test Client";
        String webhookUrl = "https://test.com/webhook";
        
        Client client = new Client();
        client.setName(name);
        client.setWebhookUrl(webhookUrl);
        
        assertEquals(name, client.getName());
        assertEquals(webhookUrl, client.getWebhookUrl());
    }

    @Test
    void whenCreateClientWithBuilder_thenFieldsAreSetCorrectly() {
        String name = "Test Client";
        String webhookUrl = "https://test.com/webhook";
        
        Client client = Client.builder()
                .name(name)
                .webhookUrl(webhookUrl)
                .build();
        
        assertEquals(name, client.getName());
        assertEquals(webhookUrl, client.getWebhookUrl());
    }
} 