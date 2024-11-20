package com.restaurant.creditmanagement.repository;

import com.restaurant.creditmanagement.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCustomerIdOrderByTransactionDateDesc(Long customerId);
    
    List<Transaction> findByCustomerId(Long customerId);
    
    @Modifying
    @Query("DELETE FROM Transaction t WHERE t.customer.id = :customerId")
    void deleteByCustomerId(Long customerId);
}
