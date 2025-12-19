package com.restaurant.creditmanagement.service;

// Add this import
import java.time.format.DateTimeFormatter;

import com.restaurant.creditmanagement.model.MenuItem;
import com.restaurant.creditmanagement.model.Order;
import com.restaurant.creditmanagement.model.OrderItem;
import com.restaurant.creditmanagement.repository.OrderItemRepository;
import com.restaurant.creditmanagement.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public BigDecimal calculateTotalRevenue(String adminId) {
        List<Order> orders = orderRepository.findByAdminId(adminId);
        return orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    private OrderItemRepository orderItemRepository;  // Add this line

    public Order createOrder(Order order, List<String> menuItemIds, List<Integer> quantities) {
        // Set creation timestamp
        order.setCreatedAt(LocalDateTime.now());

        // Save the order first
        Order savedOrder = orderRepository.save(order);

        // Create and save order items
        List<String> orderItemIds = new ArrayList<>();
        for (int i = 0; i < menuItemIds.size(); i++) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(savedOrder.getId());
            orderItem.setMenuItemId(menuItemIds.get(i));
            orderItem.setQuantity(quantities.get(i));

            // Get menu item to set unit price
            MenuItem menuItem = menuItemService.getMenuItemById(menuItemIds.get(i));
            if (menuItem != null) {
                orderItem.setUnitPrice(menuItem.getPrice());
            }

            OrderItem savedItem = orderItemRepository.save(orderItem);
            orderItemIds.add(savedItem.getId());
        }

        // Update order with order item IDs
        savedOrder.setOrderItemIds(orderItemIds);
        orderRepository.save(savedOrder);

        return savedOrder;
    }

    public List<Order> getOrdersByAdminId(String adminId) {
        return orderRepository.findByAdminId(adminId);
    }

    public List<Order> getAllOrdersByAdmin(String adminId) {
        return orderRepository.findByAdminId(adminId);
    }

    public List<Order> getRecentOrders(String adminId) {
        return orderRepository.findByAdminIdOrderByCreatedAtDesc(adminId)
                .stream()
                .limit(5)
                .collect(Collectors.toList());
    }

    public BigDecimal calculateTotalOutstandingCredit(String adminId) {
        List<Order> creditOrders = orderRepository.findByAdminIdAndPaymentMethod(adminId, "CREDIT");
        return creditOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public long getTotalOrderCount(String adminId) {
        return orderRepository.countByAdminId(adminId);
    }

    public Order getOrderById(String id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public void deleteOrder(String id) {
        Order order = getOrderById(id);
        // First delete all associated order items
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
        orderItemRepository.deleteAll(orderItems);
        // Then delete the order
        orderRepository.delete(order);
    }

    public void updateOrderQuantities(String orderId, Map<String, String> quantities) {
        Order order = getOrderById(orderId);
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (String orderItemId : order.getOrderItemIds()) {
            OrderItem item = orderItemRepository.findById(orderItemId).orElse(null);
            if (item != null) {
                String quantityStr = quantities.get(item.getId());
                if (quantityStr != null) {
                    int newQuantity = Integer.parseInt(quantityStr);
                    item.setQuantity(newQuantity);
                    BigDecimal itemPrice = item.getUnitPrice();
                    BigDecimal itemTotal = itemPrice.multiply(BigDecimal.valueOf(newQuantity));
                    totalAmount = totalAmount.add(itemTotal);
                    orderItemRepository.save(item);
                }
            }
        }

        order.setTotalAmount(totalAmount);
        orderRepository.save(order);
    }

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
    public List<Map<String, Object>> getTopSellingItems(String adminId) {
        List<Map<String, Object>> topItems = new ArrayList<>();
        List<Order> orders = orderRepository.findByAdminId(adminId);

        Map<String, Integer> quantityMap = new HashMap<>();
        Map<String, Double> revenueMap = new HashMap<>();

        for (Order order : orders) {
            for (String orderItemId : order.getOrderItemIds()) {
                OrderItem item = orderItemRepository.findById(orderItemId).orElse(null);
                if (item != null) {
                    MenuItem menuItem = menuItemService.getMenuItemById(item.getMenuItemId());
                    if (menuItem != null) {
                        String itemName = menuItem.getName();
                        quantityMap.merge(itemName, item.getQuantity(), Integer::sum);
                        revenueMap.merge(itemName,
                            item.getQuantity() * item.getUnitPrice().doubleValue(),
                            Double::sum);
                    }
                }
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

    public List<Map<String, Object>> getCategoryPerformance(String adminId) {
        List<Map<String, Object>> performance = new ArrayList<>();
        List<Order> orders = orderRepository.findByAdminId(adminId);

        Map<String, Integer> orderCount = new HashMap<>();
        Map<String, Double> revenueCount = new HashMap<>();

        for (Order order : orders) {
            for (String orderItemId : order.getOrderItemIds()) {
                OrderItem item = orderItemRepository.findById(orderItemId).orElse(null);
                if (item != null) {
                    MenuItem menuItem = menuItemService.getMenuItemById(item.getMenuItemId());
                    if (menuItem != null) {
                        String category = menuItem.getCategory();
                        orderCount.merge(category, 1, Integer::sum);
                        revenueCount.merge(category,
                            item.getQuantity() * item.getUnitPrice().doubleValue(),
                            Double::sum);
                    }
                }
            }
        }

        orderCount.forEach((category, orderTotal) -> {
            Map<String, Object> categoryData = new HashMap<>();
            categoryData.put("category", category);
            categoryData.put("orders", orderTotal);
            categoryData.put("revenue", revenueCount.get(category));
            performance.add(categoryData);
        });

        return performance;
    }

    public List<Map<String, Object>> getPeakHours(String adminId) {
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

    public List<Map<String, Object>> getWeeklyTrends(String adminId) {
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

    public double getAverageOrderValue(String adminId) {
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





