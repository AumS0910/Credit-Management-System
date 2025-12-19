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
    List<Customer> findByAdminId(Long adminId);
    
    @Query("SELECT c FROM Customer c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) AND c.adminId = :adminId")
    List<Customer> searchByName(@Param("query") String query, @Param("adminId") Long adminId);
    
    List<Customer> findTop5ByAdminIdOrderByCreditBalanceDesc(Long adminId);
    
    @Query("SELECT COALESCE(SUM(c.creditBalance), 0) FROM Customer c WHERE c.adminId = :adminId")
    BigDecimal getTotalOutstandingCreditByAdminId(@Param("adminId") Long adminId);
    
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.adminId = :adminId")
    long countCustomersByAdminId(@Param("adminId") Long adminId);
    
    Long countByAdminId(Long adminId);
}
