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
@CrossOrigin(origins = {"http://localhost:3000", "https://credit-management-system.vercel.app"})
public class DashboardController {
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private CustomerService customerService;

    @GetMapping("/dashboard/{adminId}")
    public ResponseEntity<?> getDashboardStats(@PathVariable String adminId) {
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

            // Get recent customers (last 5 customers)
            List<Map<String, Object>> recentCustomers = customers.stream()
                .limit(5)
                .map(customer -> {
                    Map<String, Object> customerMap = new HashMap<>();
                    customerMap.put("id", customer.getId());
                    customerMap.put("name", customer.getName());
                    customerMap.put("creditBalance", customer.getCreditBalance());
                    customerMap.put("createdAt", customer.getCreatedAt());
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
            response.put("recentCustomers", recentCustomers);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching dashboard data: " + e.getMessage());
        }
    }
}