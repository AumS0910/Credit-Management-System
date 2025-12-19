package com.restaurant.creditmanagement.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "orders")
public class Order {
    @Id
    private String id;

    private String customerId;

    private List<String> orderItemIds;

    private String adminId;

    private BigDecimal totalAmount;

    private LocalDateTime orderDate;

    private String status;

    private String paymentMethod;

    private String notes;

    private BigDecimal tax;

    private LocalDateTime createdAt;
}
