package com.restaurant.creditmanagement.repository;

import com.restaurant.creditmanagement.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCustomerIdOrderByTransactionDateDesc(Long customerId);
    List<Transaction> findByCustomer_IdOrderByTransactionDateDesc(Long customerId);  // Alternative method
    List<Transaction> findByAdminIdOrderByTransactionDateDesc(Long adminId);
}
