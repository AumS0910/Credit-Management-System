package com.restaurant.creditmanagement.repository;

import com.restaurant.creditmanagement.model.Order;
import com.restaurant.creditmanagement.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);
}
