package com.restaurant.creditmanagement.repository;

import com.restaurant.creditmanagement.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByCustomerIdOrderByTransactionDateDesc(String customerId);
    List<Transaction> findByAdminIdOrderByTransactionDateDesc(String adminId);
}
