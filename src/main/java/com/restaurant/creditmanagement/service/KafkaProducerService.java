package com.restaurant.creditmanagement.service;

import com.restaurant.creditmanagement.events.OrderEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    private static final String ORDER_CREATED_TOPIC = "order-created";
    private static final String ORDER_STATUS_CHANGED_TOPIC = "order-status-changed";
    private static final String ORDER_CANCELLED_TOPIC = "order-cancelled";

    public void sendOrderCreatedEvent(OrderEvent event) {
        kafkaTemplate.send(ORDER_CREATED_TOPIC, event.getOrderId(), event);
        System.out.println("Order created event sent: " + event.getOrderId());
    }

    public void sendOrderStatusChangedEvent(OrderEvent event) {
        kafkaTemplate.send(ORDER_STATUS_CHANGED_TOPIC, event.getOrderId(), event);
        System.out.println("Order status changed event sent: " + event.getOrderId() +
                          " from " + event.getPreviousStatus() + " to " + event.getNewStatus());
    }

    public void sendOrderCancelledEvent(OrderEvent event) {
        kafkaTemplate.send(ORDER_CANCELLED_TOPIC, event.getOrderId(), event);
        System.out.println("Order cancelled event sent: " + event.getOrderId());
    }
}