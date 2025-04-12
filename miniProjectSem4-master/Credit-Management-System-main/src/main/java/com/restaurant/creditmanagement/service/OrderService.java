package com.restaurant.creditmanagement.service;

import com.restaurant.creditmanagement.model.*;

import com.restaurant.creditmanagement.repository.OrderItemRepository;
import com.restaurant.creditmanagement.repository.OrderRepository;
import com.restaurant.creditmanagement.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    private OrderItemRepository orderItemRepository;  // Add this line

    @Transactional
    public Order createOrder(Order order, List<Long> menuItemIds, List<Integer> quantities) {
        // Set creation timestamp
        order.setCreatedAt(LocalDateTime.now());
        
        // Save the order first
        Order savedOrder = orderRepository.save(order);
        
        // Create and save order items
        for (int i = 0; i < menuItemIds.size(); i++) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setMenuItem(menuItemService.getMenuItemById(menuItemIds.get(i)));
            orderItem.setQuantity(quantities.get(i));
            orderItemRepository.save(orderItem);
        }
        
        return savedOrder;
    }

    public List<Order> getOrdersByAdminId(Long adminId) {
        return orderRepository.findByAdminId(adminId);
    }

    public List<Order> getAllOrdersByAdmin(Long adminId) {
        return orderRepository.findByAdminId(adminId);
    }

    public List<Order> getRecentOrders(Long adminId) {
        return orderRepository.findByAdminIdOrderByCreatedAtDesc(adminId)
                .stream()
                .limit(5)
                .collect(Collectors.toList());
    }

    public BigDecimal calculateTotalOutstandingCredit(Long adminId) {
        List<Order> creditOrders = orderRepository.findByAdminIdAndPaymentMethod(adminId, "CREDIT");
        return creditOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getTotalOrderCount(Long adminId) {
        return orderRepository.countByAdminId(adminId);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = getOrderById(id);
        // First delete all associated order items
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        orderItemRepository.deleteAll(orderItems);
        // Then delete the order
        orderRepository.delete(order);
    }

    @Transactional
    public void updateOrderQuantities(Long orderId, Map<String, String> quantities) {
        Order order = getOrderById(orderId);
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItem item : order.getOrderItems()) {
            String quantityStr = quantities.get(item.getId().toString());
            if (quantityStr != null) {
                int newQuantity = Integer.parseInt(quantityStr);
                item.setQuantity(newQuantity);
                BigDecimal itemPrice = item.getMenuItem().getPrice();
                BigDecimal itemTotal = itemPrice.multiply(BigDecimal.valueOf(newQuantity));
                totalAmount = totalAmount.add(itemTotal);
            }
        }

        order.setTotalAmount(totalAmount);
        orderRepository.save(order);
    }
}
