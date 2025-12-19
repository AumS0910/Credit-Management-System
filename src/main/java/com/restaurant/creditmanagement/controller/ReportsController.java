package com.restaurant.creditmanagement.controller;

import com.restaurant.creditmanagement.model.Customer;
import com.restaurant.creditmanagement.model.Order;
import com.restaurant.creditmanagement.service.CustomerService;
import com.restaurant.creditmanagement.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = {"http://localhost:3000", "https://credit-management-system.vercel.app"})
public class ReportsController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/detailed")
    public ResponseEntity<?> getDetailedAnalytics(@RequestHeader("Admin-ID") String adminId) {
        if (adminId == null) {
            return ResponseEntity.badRequest().body("Admin ID is required");
        }

        Map<String, Object> response = new HashMap<>();
        
        // Menu Analytics
        Map<String, Object> menuAnalytics = new HashMap<>();
        
        // Top Selling Items
        List<Map<String, Object>> topSellingItems = orderService.getTopSellingItems(adminId);
        menuAnalytics.put("topSellingItems", topSellingItems);
        
        // Category Performance
        List<Map<String, Object>> categoryPerformance = orderService.getCategoryPerformance(adminId);
        menuAnalytics.put("categoryPerformance", categoryPerformance);
        
        // Time-based Analysis
        Map<String, Object> timeBasedAnalysis = new HashMap<>();
        timeBasedAnalysis.put("peakHours", orderService.getPeakHours(adminId));
        timeBasedAnalysis.put("weeklyTrends", orderService.getWeeklyTrends(adminId));
        menuAnalytics.put("timeBasedAnalysis", timeBasedAnalysis);
        
        // Customer Analytics
        Map<String, Object> customerAnalytics = new HashMap<>();
        customerAnalytics.put("loyaltyDistribution", customerService.getLoyaltyDistribution(adminId));
        customerAnalytics.put("orderFrequency", customerService.getOrderFrequency(adminId));
        customerAnalytics.put("averageOrderValue", orderService.getAverageOrderValue(adminId));

        response.put("menuAnalytics", menuAnalytics);
        response.put("customerAnalytics", customerAnalytics);

        return ResponseEntity.ok(response);
    }


}
