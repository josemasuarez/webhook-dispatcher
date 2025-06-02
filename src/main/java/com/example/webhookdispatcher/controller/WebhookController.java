package com.example.webhookdispatcher.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/webhooks")
@Slf4j
@RequiredArgsConstructor
public class WebhookController {

    @PostMapping("/receive")
    public ResponseEntity<String> receiveWebhook(@RequestBody String payload) {
        log.info("Received Webhook: {}", payload);
        return ResponseEntity.ok("Webhook received successfully");
    }
} 