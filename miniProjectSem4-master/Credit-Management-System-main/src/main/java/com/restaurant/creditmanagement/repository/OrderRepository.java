package com.restaurant.creditmanagement.repository;

import com.restaurant.creditmanagement.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByAdminId(Long adminId);
    Long countByAdminId(Long adminId);
    List<Order> findByAdminIdAndPaymentMethod(Long adminId, String paymentMethod);
    List<Order> findByAdminIdOrderByCreatedAtDesc(Long adminId);
    Integer countByCustomerId(Long customerId);
    
    // Add this new method
    List<Order> findTop5ByAdminIdOrderByCreatedAtDesc(Long adminId);
}
