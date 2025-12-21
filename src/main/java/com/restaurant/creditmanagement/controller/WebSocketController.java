package com.restaurant.creditmanagement.controller;

import com.restaurant.creditmanagement.model.Order;
import com.restaurant.creditmanagement.service.RedisMessagePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private RedisMessagePublisher redisPublisher;

    // Method to send order updates to all connected clients
    public void sendOrderUpdate(Order order, String action) {
        OrderUpdateMessage message = new OrderUpdateMessage(
            order.getId(),
            order.getStatus(),
            action,
            order.getTotalAmount(),
            order.getCustomerId()
        );

        // Send to all clients subscribed to /topic/orders
        messagingTemplate.convertAndSend("/topic/orders", message);

        // Also publish to Redis for pub/sub
        try {
            redisPublisher.publish(message);
        } catch (Exception e) {
            System.err.println("Failed to publish to Redis: " + e.getMessage());
        }
    }

    // Method to send notifications
    public void sendNotification(String type, String message, String orderId) {
        NotificationMessage notification = new NotificationMessage(type, message, orderId);
        messagingTemplate.convertAndSend("/topic/notifications", notification);

        // Also publish to Redis for pub/sub
        try {
            redisPublisher.publish(notification);
        } catch (Exception e) {
            System.err.println("Failed to publish notification to Redis: " + e.getMessage());
        }
    }

    // Inner classes for message payloads
    public static class OrderUpdateMessage {
        private String orderId;
        private String status;
        private String action;
        private java.math.BigDecimal totalAmount;
        private String customerId;

        public OrderUpdateMessage(String orderId, String status, String action,
                                java.math.BigDecimal totalAmount, String customerId) {
            this.orderId = orderId;
            this.status = status;
            this.action = action;
            this.totalAmount = totalAmount;
            this.customerId = customerId;
        }

        // Getters
        public String getOrderId() { return orderId; }
        public String getStatus() { return status; }
        public String getAction() { return action; }
        public java.math.BigDecimal getTotalAmount() { return totalAmount; }
        public String getCustomerId() { return customerId; }
    }

    public static class NotificationMessage {
        private String type;
        private String message;
        private String orderId;

        public NotificationMessage(String type, String message, String orderId) {
            this.type = type;
            this.message = message;
            this.orderId = orderId;
        }

        // Getters
        public String getType() { return type; }
        public String getMessage() { return message; }
        public String getOrderId() { return orderId; }
    }
}