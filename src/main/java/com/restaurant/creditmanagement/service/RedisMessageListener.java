package com.restaurant.creditmanagement.service;

import com.restaurant.creditmanagement.controller.WebSocketController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class RedisMessageListener implements MessageListener {

    @Autowired
    private WebSocketController webSocketController;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());

        System.out.println("Received message from Redis channel '" + channel + "': " + body);

        try {
            // Here you could add logic to:
            // 1. Send email notifications
            // 2. Send SMS notifications
            // 3. Trigger other microservices
            // 4. Log events for analytics

            // For now, we'll just broadcast to WebSocket clients
            // In a real event-driven system, this would trigger various services

        } catch (Exception e) {
            System.err.println("Error processing Redis message: " + e.getMessage());
        }
    }
}