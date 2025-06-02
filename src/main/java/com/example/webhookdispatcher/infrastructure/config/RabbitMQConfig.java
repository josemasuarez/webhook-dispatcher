package com.example.webhookdispatcher.infrastructure.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.webhook-delivery}")
    private String webhookDeliveryQueue;

    @Value("${rabbitmq.queue.webhook-delivery-dlq}")
    private String webhookDeliveryDlq;

    @Value("${rabbitmq.exchange.webhook-delivery}")
    private String webhookDeliveryExchange;

    @Value("${rabbitmq.routing-key.webhook-delivery}")
    private String webhookDeliveryRoutingKey;

    @Value("${rabbitmq.routing-key.webhook-delivery-dlq}")
    private String webhookDeliveryDlqRoutingKey;

    @Bean
    public Queue webhookDeliveryQueue() {
        return QueueBuilder.durable(webhookDeliveryQueue)
                .withArgument("x-dead-letter-exchange", webhookDeliveryExchange)
                .withArgument("x-dead-letter-routing-key", webhookDeliveryDlqRoutingKey)
                .build();
    }

    @Bean
    public Queue webhookDeliveryDlq() {
        return QueueBuilder.durable(webhookDeliveryDlq).build();
    }

    @Bean
    public DirectExchange webhookDeliveryExchange() {
        return new DirectExchange(webhookDeliveryExchange);
    }

    @Bean
    public Binding webhookDeliveryBinding() {
        return BindingBuilder.bind(webhookDeliveryQueue())
                .to(webhookDeliveryExchange())
                .with(webhookDeliveryRoutingKey);
    }

    @Bean
    public Binding webhookDeliveryDlqBinding() {
        return BindingBuilder.bind(webhookDeliveryDlq())
                .to(webhookDeliveryExchange())
                .with(webhookDeliveryDlqRoutingKey);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
            @Qualifier("jsonMessageConverter") MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    public RejectAndDontRequeueRecoverer rejectAndDontRequeueRecoverer() {
        return new RejectAndDontRequeueRecoverer();
    }

    @Bean
    public RetryOperationsInterceptor retryOperationsInterceptor(RejectAndDontRequeueRecoverer recoverer) {
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);

        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(retryPolicy);

        return RetryInterceptorBuilder.stateless()
                .retryPolicy(retryPolicy)
                .backOffOptions(1000, 2.0, 10000)
                .recoverer(recoverer)
                .build();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            RetryOperationsInterceptor retryOperationsInterceptor,
            @Qualifier("jsonMessageConverter") MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setAdviceChain(retryOperationsInterceptor);
        factory.setDefaultRequeueRejected(false);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        factory.setPrefetchCount(1);
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(5);
        factory.setChannelTransacted(true);
        factory.setReceiveTimeout(5000L);
        factory.setMissingQueuesFatal(false);
        return factory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory dlqListenerContainerFactory(
            ConnectionFactory connectionFactory,
            @Qualifier("jsonMessageConverter") MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setAcknowledgeMode(AcknowledgeMode.NONE);
        return factory;
    }
} 