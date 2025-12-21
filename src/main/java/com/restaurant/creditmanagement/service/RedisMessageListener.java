package com.restaurant.creditmanagement.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

// Import the inner classes from WebSocketController
import com.restaurant.creditmanagement.controller.WebSocketController.OrderUpdateMessage;
import com.restaurant.creditmanagement.controller.WebSocketController.NotificationMessage;

@Service
public class RedisMessageListener implements MessageListener {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private ObjectMapper objectMapper;

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

            // Process notifications based on channel
            if ("orders".equals(channel)) {
                processOrderNotification(body);
            } else if ("notifications".equals(channel)) {
                processGeneralNotification(body);
            }

        } catch (Exception e) {
            System.err.println("Error processing Redis message: " + e.getMessage());
        }
    }

    private void processOrderNotification(String messageBody) {
        try {
            // Parse the order update message
            OrderUpdateMessage orderUpdate =
                objectMapper.readValue(messageBody, OrderUpdateMessage.class);

            // Send email/SMS notifications based on order status
            // Note: In a real implementation, you'd need to fetch customer details from database
            if ("COMPLETED".equals(orderUpdate.getStatus())) {
                // Send order completion notifications
                System.out.println("Order " + orderUpdate.getOrderId() + " completed - sending notifications");
                // TODO: Fetch customer details and send actual notifications
                // emailService.sendOrderStatusEmail(customerEmail, customerName, orderId, "Completed");
                // smsService.sendOrderStatusSms(customerPhone, customerName, orderId, "Completed");
            } else if ("CANCELLED".equals(orderUpdate.getStatus())) {
                // Send order cancellation notifications
                System.out.println("Order " + orderUpdate.getOrderId() + " cancelled - sending notifications");
                // TODO: Fetch customer details and send actual notifications
                // emailService.sendOrderStatusEmail(customerEmail, customerName, orderId, "Cancelled");
                // smsService.sendOrderStatusSms(customerPhone, customerName, orderId, "Cancelled");
            } else if ("APPROVED".equals(orderUpdate.getStatus())) {
                // Send order approval notifications
                System.out.println("Order " + orderUpdate.getOrderId() + " approved - sending notifications");
            }

        } catch (Exception e) {
            System.err.println("Error processing order notification: " + e.getMessage());
        }
    }

    private void processGeneralNotification(String messageBody) {
        try {
            // Parse general notifications
            NotificationMessage notification =
                objectMapper.readValue(messageBody, NotificationMessage.class);

            System.out.println("Processing notification: " + notification.getType() + " - " + notification.getMessage());

            // Here you could send different types of notifications
            // For example, system alerts, payment confirmations, etc.

        } catch (Exception e) {
            System.err.println("Error processing general notification: " + e.getMessage());
        }
    }
}