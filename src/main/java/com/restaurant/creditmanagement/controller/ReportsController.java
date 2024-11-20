package com.restaurant.creditmanagement.controller;

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

@Controller
@RequestMapping("/reports")
public class ReportsController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String showReports(Model model, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/login";
        }

        var customers = customerService.getAllCustomers();
        var orders = orderService.getAllOrders();

        // Calculate statistics
        long activeCustomers = customers.stream()
                .filter(c -> c.getCreditBalance().compareTo(BigDecimal.ZERO) > 0)
                .count();

        long todayOrders = orders.stream()
                .filter(o -> o.getOrderDate().toLocalDate().equals(LocalDate.now()))
                .count();

        BigDecimal totalCredit = customers.stream()
                .map(c -> c.getTotalCredit())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal outstandingCredit = customers.stream()
                .map(c -> c.getCreditBalance())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Add all data to model
        model.addAttribute("customers", customers);
        model.addAttribute("orders", orders);
        model.addAttribute("activeCustomers", activeCustomers);
        model.addAttribute("todayOrders", todayOrders);
        model.addAttribute("totalCredit", totalCredit);
        model.addAttribute("outstandingCredit", outstandingCredit);
        model.addAttribute("adminName", session.getAttribute("adminName"));

        return "reports/index";
    }
}
