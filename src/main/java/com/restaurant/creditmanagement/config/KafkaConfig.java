package com.restaurant.creditmanagement.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    // Order-related topics
    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name("order-created")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderStatusChangedTopic() {
        return TopicBuilder.name("order-status-changed")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderCancelledTopic() {
        return TopicBuilder.name("order-cancelled")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // Payment-related topics
    @Bean
    public NewTopic paymentProcessedTopic() {
        return TopicBuilder.name("payment-processed")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // Notification topics
    @Bean
    public NewTopic customerNotifiedTopic() {
        return TopicBuilder.name("customer-notified")
                .partitions(3)
                .replicas(1)
                .build();
    }
}