package com.restaurant.creditmanagement.service;

import com.restaurant.creditmanagement.model.Transaction;
import java.util.List;

public interface TransactionService {
    Transaction saveTransaction(Transaction transaction);
    List<Transaction> getTransactionsByCustomerId(Long customerId);
    List<Transaction> getTransactionsByAdminId(Long adminId);
}