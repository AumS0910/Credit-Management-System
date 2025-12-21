package com.restaurant.creditmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class RedisMessageListener implements MessageListener {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());

        System.out.println("Received message from Redis channel '" + channel + "': " + body);

        try {
            // Publish a Spring application event that can be handled by other components
            // This breaks the circular dependency by using event-driven communication
            RedisMessageEvent event = new RedisMessageEvent(this, channel, body);
            eventPublisher.publishEvent(event);

            // Here you could add logic to:
            // 1. Send email notifications
            // 2. Send SMS notifications
            // 3. Trigger other microservices
            // 4. Log events for analytics

        } catch (Exception e) {
            System.err.println("Error processing Redis message: " + e.getMessage());
        }
    }
}