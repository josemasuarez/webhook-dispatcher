package com.example.webhookdispatcher.infrastructure.config;

import com.example.webhookdispatcher.infrastructure.rabbitmq.WebhookMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.AbstractMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MessageConverterConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    @Primary
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {

        return new AbstractMessageConverter() {

            @Override
            protected Message createMessage(Object object, MessageProperties messageProperties) {
                try {
                    byte[] bytes = objectMapper.writeValueAsBytes(object);
                    messageProperties.setContentType("application/json");
                    return new Message(bytes, messageProperties);
                } catch (IOException e) {
                    throw new MessageConversionException("Error converting message to JSON", e);
                }
            }

            @Override
            public Object fromMessage(Message message) throws MessageConversionException {
                try {
                    String content = new String(message.getBody(), StandardCharsets.UTF_8);
                    return objectMapper.readValue(content, WebhookMessage.class);
                } catch (IOException e) {
                    throw new MessageConversionException("Error converting message to WebhookMessage", e);
                }
            }
        };
    }
} 