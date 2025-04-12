package com.restaurant.creditmanagement.controller;

import com.restaurant.creditmanagement.model.Customer;
import com.restaurant.creditmanagement.model.Order;
import com.restaurant.creditmanagement.service.CustomerService;
import com.restaurant.creditmanagement.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportsController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String showReports(Model model, HttpSession session) {
        Long adminId = (Long) session.getAttribute("adminId");
        if (adminId == null) {
            return "redirect:/login";
        }

        // Update to pass adminId to service methods
        List<Customer> customers = customerService.getAllCustomers(adminId);
        List<Order> orders = orderService.getAllOrdersByAdmin(adminId);
        
        // Calculate statistics
        model.addAttribute("customers", customers);
        model.addAttribute("orders", orders);
        
        return "reports/index";
    }
}
