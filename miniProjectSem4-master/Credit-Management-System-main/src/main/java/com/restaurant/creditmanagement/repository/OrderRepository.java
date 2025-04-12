package com.restaurant.creditmanagement.repository;

import com.restaurant.creditmanagement.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByAdminId(Long adminId);
    List<Order> findByAdminIdOrderByCreatedAtDesc(Long adminId);
    List<Order> findByAdminIdAndPaymentMethod(Long adminId, String paymentMethod);
    Long countByAdminId(Long adminId);
    List<Order> findTop5ByAdminIdOrderByCreatedAtDesc(Long adminId);  // Added this method
}
