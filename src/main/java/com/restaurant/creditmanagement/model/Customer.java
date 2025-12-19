package com.restaurant.creditmanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_seq")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false,unique = true)
    private String email;

    @Column
    private String address;

    @Column(name = "total_credit", nullable = false)
    private BigDecimal totalCredit = BigDecimal.ZERO;

    @Column(name = "credit_balance", nullable = false)
    private BigDecimal creditBalance = BigDecimal.ZERO;

    // Add a specific getter for creditBalance that never returns null
    public BigDecimal getCreditBalance() {
        return creditBalance != null ? creditBalance : BigDecimal.ZERO;
    }

    // Add a specific setter that handles null values
    public void setCreditBalance(BigDecimal creditBalance) {
        this.creditBalance = creditBalance != null ? creditBalance : BigDecimal.ZERO;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (totalCredit == null) {
            totalCredit = BigDecimal.ZERO;
        }
        if (creditBalance == null) {
            creditBalance = BigDecimal.ZERO;
        }
    }

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    @OneToMany(mappedBy = "customerId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    @JsonIgnoreProperties("customer")
    @OneToMany(mappedBy = "customer")
    private List<Order> orders;



    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean active = true;

    // Add these getter and setter methods
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}