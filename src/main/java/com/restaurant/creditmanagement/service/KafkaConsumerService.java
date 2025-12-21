package com.restaurant.creditmanagement.service;

import com.restaurant.creditmanagement.events.OrderEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "order-created", groupId = "restaurant-group")
    public void consumeOrderCreatedEvent(OrderEvent event) {
        System.out.println("=== ORDER CREATED EVENT ===");
        System.out.println("Event ID: " + event.getEventId());
        System.out.println("Order ID: " + event.getOrderId());
        System.out.println("Customer: " + event.getCustomerName() + " (" + event.getCustomerId() + ")");
        System.out.println("Total Amount: $" + event.getTotalAmount());
        System.out.println("Payment Method: " + event.getPaymentMethod());
        System.out.println("Status: " + event.getNewStatus());
        System.out.println("Timestamp: " + event.getEventTimestamp());
        if (event.getOrderItems() != null) {
            System.out.println("Order Items:");
            event.getOrderItems().forEach(item -> {
                System.out.println("  - " + item.getMenuItemName() + " x" + item.getQuantity() +
                                 " = $" + item.getTotalPrice());
            });
        }
        System.out.println("========================\n");
    }

    @KafkaListener(topics = "order-status-changed", groupId = "restaurant-group")
    public void consumeOrderStatusChangedEvent(OrderEvent event) {
        System.out.println("=== ORDER STATUS CHANGED EVENT ===");
        System.out.println("Event ID: " + event.getEventId());
        System.out.println("Order ID: " + event.getOrderId());
        System.out.println("Status Change: " + event.getPreviousStatus() + " → " + event.getNewStatus());
        System.out.println("Customer ID: " + event.getCustomerId());
        System.out.println("Total Amount: $" + event.getTotalAmount());
        System.out.println("Timestamp: " + event.getEventTimestamp());
        System.out.println("===============================\n");
    }

    @KafkaListener(topics = "order-cancelled", groupId = "restaurant-group")
    public void consumeOrderCancelledEvent(OrderEvent event) {
        System.out.println("=== ORDER CANCELLED EVENT ===");
        System.out.println("Event ID: " + event.getEventId());
        System.out.println("Order ID: " + event.getOrderId());
        System.out.println("Previous Status: " + event.getPreviousStatus());
        System.out.println("Customer ID: " + event.getCustomerId());
        System.out.println("Total Amount: $" + event.getTotalAmount());
        System.out.println("Timestamp: " + event.getEventTimestamp());
        System.out.println("===========================\n");
    }
}