package com.example.webhookdispatcher.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    private UUID id;
    private String name;
    private String webhookUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}