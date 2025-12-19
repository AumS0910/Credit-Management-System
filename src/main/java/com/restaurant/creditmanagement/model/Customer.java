package com.restaurant.creditmanagement.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "customers")
public class Customer {
    @Id
    private String id;

    private String name;
    private String phone;
    private String email;
    private String address;
    private BigDecimal totalCredit = BigDecimal.ZERO;
    private BigDecimal creditBalance = BigDecimal.ZERO;
    private LocalDateTime createdAt;
    private String adminId; // Changed from Long to String for MongoDB
    private List<String> transactionIds = new ArrayList<>(); // Reference to transactions
    private List<String> orderIds = new ArrayList<>(); // Reference to orders
    private boolean active = true;

    // Explicit getter/setter for boolean field
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // Custom getter/setter for creditBalance
    public BigDecimal getCreditBalance() {
        return creditBalance != null ? creditBalance : BigDecimal.ZERO;
    }

    public void setCreditBalance(BigDecimal creditBalance) {
        this.creditBalance = creditBalance != null ? creditBalance : BigDecimal.ZERO;
    }
}