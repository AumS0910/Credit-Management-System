package com.restaurant.creditmanagement.controller;

import com.restaurant.creditmanagement.model.Customer;
import com.restaurant.creditmanagement.model.Order;
import com.restaurant.creditmanagement.service.CustomerService;
import com.restaurant.creditmanagement.service.DashboardService;
import com.restaurant.creditmanagement.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DashboardController {
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private CustomerService customerService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        Long adminId = (Long) session.getAttribute("adminId");
        if (adminId == null) {
            return "redirect:/login";
        }

        // Get all customers for this admin
        List<Customer> customers = customerService.getAllCustomers(adminId);
        
        // Calculate total outstanding credit correctly
        BigDecimal totalOutstandingCredit = customers.stream()
                .map(Customer::getCreditBalance)
                .filter(balance -> balance.compareTo(BigDecimal.ZERO) > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Get recent orders (last 5) - Updated method call
        List<Order> recentOrders = orderService.getRecentOrders(adminId);

        // Get top customers (by credit balance)
        List<Customer> topCustomers = customers.stream()
                .sorted((c1, c2) -> c2.getCreditBalance().compareTo(c1.getCreditBalance()))
                .limit(5)
                .collect(Collectors.toList());

        model.addAttribute("totalOutstandingCredit", totalOutstandingCredit);
        model.addAttribute("totalCustomers", customers.size());
        model.addAttribute("totalOrders", orderService.getTotalOrderCount(adminId));
        model.addAttribute("recentOrders", recentOrders);
        model.addAttribute("topCustomers", topCustomers);
        model.addAttribute("adminUsername", session.getAttribute("adminUsername"));

        return "dashboard";
    }
}