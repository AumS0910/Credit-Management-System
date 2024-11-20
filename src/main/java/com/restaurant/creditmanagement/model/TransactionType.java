package com.restaurant.creditmanagement.model;

public enum TransactionType {
    CREDIT,    // When credit is issued to customer
    DEBIT,     // When credit is used by customer
    SETTLEMENT // When outstanding balance is settled
}
