package com.restaurant.creditmanagement.controller;

import com.restaurant.creditmanagement.model.Customer;
import com.restaurant.creditmanagement.model.Order;
import com.restaurant.creditmanagement.service.CustomerService;
import com.restaurant.creditmanagement.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class DashboardController {
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private CustomerService customerService;

    @GetMapping("/dashboard/{adminId}")
    public ResponseEntity<?> getDashboardStats(@PathVariable Long adminId) {
        try {
            // Get all customers for this admin
            List<Customer> customers = customerService.getAllCustomers(adminId);
            
            // Calculate total revenue
            BigDecimal totalRevenue = orderService.calculateTotalRevenue(adminId);

            // Calculate total outstanding credit
            BigDecimal totalCreditBalance = customers.stream()
                    .map(Customer::getCreditBalance)
                    .filter(balance -> balance.compareTo(BigDecimal.ZERO) > 0)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Get recent customers (last 5 customers with orders)
            List<Map<String, Object>> recentCustomers = customers.stream()
                .filter(customer -> !customer.getOrders().isEmpty())
                .sorted((c1, c2) -> {
                    LocalDateTime lastOrder1 = c1.getOrders().stream()
                        .map(Order::getOrderDate)
                        .max(LocalDateTime::compareTo)
                        .orElse(LocalDateTime.MIN);
                    LocalDateTime lastOrder2 = c2.getOrders().stream()
                        .map(Order::getOrderDate)
                        .max(LocalDateTime::compareTo)
                        .orElse(LocalDateTime.MIN);
                    return lastOrder2.compareTo(lastOrder1);
                })
                .limit(5)
                .map(customer -> {
                    Map<String, Object> customerMap = new HashMap<>();
                    customerMap.put("id", customer.getId());
                    customerMap.put("name", customer.getName());
                    customerMap.put("creditBalance", customer.getCreditBalance());
                    customerMap.put("lastVisit", customer.getOrders().stream()
                        .map(Order::getOrderDate)
                        .max(LocalDateTime::compareTo)
                        .orElse(null));
                    return customerMap;
                })
                .collect(Collectors.toList());

            // Create response object
            Map<String, Object> response = new HashMap<>();
            response.put("revenue", totalRevenue);
            response.put("orders", orderService.getTotalOrderCount(adminId));
            response.put("customers", customers.size());
            response.put("creditBalance", totalCreditBalance);
            response.put("recentOrders", orderService.getRecentOrders(adminId));
            response.put("recentCustomers", recentCustomers);  // Add recent customers to response

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching dashboard data: " + e.getMessage());
        }
    }
}