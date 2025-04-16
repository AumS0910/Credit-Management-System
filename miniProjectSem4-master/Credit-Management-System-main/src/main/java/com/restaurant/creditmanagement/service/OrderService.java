package com.restaurant.creditmanagement.service;

// Add this import
import java.time.format.DateTimeFormatter;

import com.restaurant.creditmanagement.model.Order;
import com.restaurant.creditmanagement.model.OrderItem;
import com.restaurant.creditmanagement.repository.OrderItemRepository;
import com.restaurant.creditmanagement.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public BigDecimal calculateTotalRevenue(Long adminId) {
        List<Order> orders = orderRepository.findByAdminId(adminId);
        return orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

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

    @Transactional
    public Order updateOrder(Order order) {
        if (order.getId() == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        
        // Verify order exists
        Order existingOrder = getOrderById(order.getId());
        if (existingOrder == null) {
            throw new IllegalArgumentException("Order not found");
        }

        // Save the updated order
        return orderRepository.save(order);
    }
    public List<Map<String, Object>> getTopSellingItems(Long adminId) {
        List<Map<String, Object>> topItems = new ArrayList<>();
        List<Order> orders = orderRepository.findByAdminId(adminId);

        Map<String, Integer> quantityMap = new HashMap<>();
        Map<String, Double> revenueMap = new HashMap<>();

        for (Order order : orders) {
            // Changed from getItems() to getOrderItems()
            for (OrderItem item : order.getOrderItems()) {
                String itemName = item.getMenuItem().getName();
                quantityMap.merge(itemName, item.getQuantity(), Integer::sum);
                revenueMap.merge(itemName, 
                    item.getQuantity() * item.getMenuItem().getPrice().doubleValue(), 
                    Double::sum);
            }
        }

        // Convert to required format
        quantityMap.forEach((name, quantity) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", name);
            item.put("quantity", quantity);
            item.put("revenue", revenueMap.get(name));
            topItems.add(item);
        });

        return topItems;
    }

    public List<Map<String, Object>> getCategoryPerformance(Long adminId) {
        List<Map<String, Object>> performance = new ArrayList<>();
        List<Order> orders = orderRepository.findByAdminId(adminId);

        Map<String, Integer> orderCount = new HashMap<>();
        Map<String, Double> revenueCount = new HashMap<>();

        for (Order order : orders) {
            for (OrderItem item : order.getOrderItems()) {
                String category = item.getMenuItem().getCategory();
                orderCount.merge(category, 1, Integer::sum);
                revenueCount.merge(category, 
                    item.getQuantity() * item.getMenuItem().getPrice().doubleValue(), 
                    Double::sum);
            }
        }

        // Changed variable name from 'orders' to 'orderTotal' to avoid conflict
        orderCount.forEach((category, orderTotal) -> {
            Map<String, Object> categoryData = new HashMap<>();
            categoryData.put("category", category);
            categoryData.put("orders", orderTotal);
            categoryData.put("revenue", revenueCount.get(category));
            performance.add(categoryData);
        });

        return performance;
    }

    public List<Map<String, Object>> getPeakHours(Long adminId) {
        List<Map<String, Object>> peakHours = new ArrayList<>();
        List<Order> orders = orderRepository.findByAdminId(adminId);

        Map<String, Integer> hourlyOrders = new HashMap<>();

        for (Order order : orders) {
            String hour = order.getOrderDate().format(DateTimeFormatter.ofPattern("HH:00"));
            hourlyOrders.merge(hour, 1, Integer::sum);
        }

        hourlyOrders.forEach((hour, count) -> {
            Map<String, Object> hourData = new HashMap<>();
            hourData.put("hour", hour);
            hourData.put("orders", count);
            peakHours.add(hourData);
        });

        return peakHours;
    }

    public List<Map<String, Object>> getWeeklyTrends(Long adminId) {
        List<Map<String, Object>> trends = new ArrayList<>();
        List<Order> orders = orderRepository.findByAdminId(adminId);

        Map<String, Integer> dailyOrders = new HashMap<>();
        Map<String, Double> dailyRevenue = new HashMap<>();

        for (Order order : orders) {
            String day = order.getOrderDate().getDayOfWeek().toString();
            dailyOrders.merge(day, 1, Integer::sum);
            // Convert BigDecimal to Double
            dailyRevenue.merge(day, order.getTotalAmount().doubleValue(), Double::sum);
        }

        dailyOrders.forEach((day, orderCount) -> {
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("day", day);
            dayData.put("orders", orderCount);
            dayData.put("revenue", dailyRevenue.get(day));
            trends.add(dayData);
        });

        return trends;
    }

    public double getAverageOrderValue(Long adminId) {
        List<Order> orders = orderRepository.findByAdminId(adminId);
        if (orders.isEmpty()) {
            return 0.0;
        }
        double totalValue = orders.stream()
                .map(Order::getTotalAmount)
                .map(BigDecimal::doubleValue)
                .mapToDouble(Double::valueOf)
                .sum();
        return totalValue / orders.size();
    }
}





