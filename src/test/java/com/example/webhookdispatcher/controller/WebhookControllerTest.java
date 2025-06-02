package com.example.webhookdispatcher.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(com.example.webhookdispatcher.infrastructure.controller.WebhookController.class)
class WebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenReceiveWebhook_thenReturnSuccess() throws Exception {
        String payload = "{\"event\":\"test\",\"data\":\"test data\"}";

        mockMvc.perform(post("/api/webhooks/receive")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().string("Webhook received successfully"));
    }
} 