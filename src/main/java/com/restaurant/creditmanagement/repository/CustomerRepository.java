package com.restaurant.creditmanagement.repository;

import com.restaurant.creditmanagement.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    @Query("SELECT c FROM Customer c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Customer> searchByName(@Param("query") String query);
    
    @Query("SELECT c FROM Customer c ORDER BY c.creditBalance DESC")
    List<Customer> findTopCustomersByCredit();

    @Query("SELECT COALESCE(SUM(c.creditBalance), 0) FROM Customer c")
    BigDecimal getTotalOutstandingCredit();

    @Query("SELECT COUNT(c) FROM Customer c")
    long countCustomers();

    @Query("SELECT c FROM Customer c ORDER BY c.creditBalance DESC")
    List<Customer> findTop5ByOrderByCreditBalanceDesc();
}
