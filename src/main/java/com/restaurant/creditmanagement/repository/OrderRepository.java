package com.restaurant.creditmanagement.repository;

import com.restaurant.creditmanagement.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByAdminId(String adminId);
    Long countByAdminId(String adminId);
    List<Order> findByAdminIdAndPaymentMethod(String adminId, String paymentMethod);
    List<Order> findByAdminIdOrderByCreatedAtDesc(String adminId);
    Integer countByCustomerId(String customerId);

    // Add this new method
    List<Order> findTop5ByAdminIdOrderByCreatedAtDesc(String adminId);
}
