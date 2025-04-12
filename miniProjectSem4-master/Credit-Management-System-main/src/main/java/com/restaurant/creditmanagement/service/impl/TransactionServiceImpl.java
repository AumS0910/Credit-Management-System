package com.restaurant.creditmanagement.service.impl;

import com.restaurant.creditmanagement.model.Transaction;
import com.restaurant.creditmanagement.repository.TransactionRepository;
import com.restaurant.creditmanagement.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getTransactionsByCustomerId(Long customerId) {
        return transactionRepository.findByCustomerIdOrderByTransactionDateDesc(customerId);
    }

    @Override
    public List<Transaction> getTransactionsByAdminId(Long adminId) {
        return transactionRepository.findByAdminIdOrderByTransactionDateDesc(adminId);
    }
}