package com.restaurant.creditmanagement.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderEvent {
    private String eventId;
    private String eventType; // "ORDER_CREATED", "ORDER_STATUS_CHANGED", "ORDER_CANCELLED"
    private String orderId;
    private String adminId;
    private String customerId;
    private String customerName;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String previousStatus;
    private String newStatus;
    private String notes;
    private LocalDateTime eventTimestamp;
    private List<OrderItemEvent> orderItems;

    // Constructors
    public OrderEvent() {}

    public OrderEvent(String eventType, String orderId, String adminId) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.eventType = eventType;
        this.orderId = orderId;
        this.adminId = adminId;
        this.eventTimestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(String previousStatus) { this.previousStatus = previousStatus; }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getEventTimestamp() { return eventTimestamp; }
    public void setEventTimestamp(LocalDateTime eventTimestamp) { this.eventTimestamp = eventTimestamp; }

    public List<OrderItemEvent> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItemEvent> orderItems) { this.orderItems = orderItems; }
}