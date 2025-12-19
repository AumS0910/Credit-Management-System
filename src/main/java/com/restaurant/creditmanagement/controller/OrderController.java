package com.restaurant.creditmanagement.controller;

import com.restaurant.creditmanagement.model.Customer;
import com.restaurant.creditmanagement.model.Order;
import com.restaurant.creditmanagement.model.PaymentMethod;
import com.restaurant.creditmanagement.service.CustomerService;
import com.restaurant.creditmanagement.service.OrderService;
import com.restaurant.creditmanagement.service.MenuItemService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = {"http://localhost:3000", "https://credit-management-system.vercel.app"})
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private MenuItemService menuItemService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest,
                                        @RequestHeader("Admin-ID") String adminId) {
        try {
            Customer customer = customerService.getCustomerById(orderRequest.getCustomerId(), adminId)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

            Order order = new Order();
            order.setAdminId(adminId);
            order.setCustomerId(orderRequest.getCustomerId());
            order.setPaymentMethod(orderRequest.getPaymentMethod());
            order.setNotes(orderRequest.getNotes());
            order.setTax(orderRequest.getTax());
            order.setTotalAmount(orderRequest.getTotalAmount());
            order.setOrderDate(LocalDateTime.now());
            order.setStatus("PENDING");

            if ("CREDIT".equalsIgnoreCase(orderRequest.getPaymentMethod())) {
                BigDecimal currentBalance = customer.getCreditBalance() != null ?
                        customer.getCreditBalance() : BigDecimal.ZERO;
                BigDecimal orderAmount = orderRequest.getTotalAmount();
                BigDecimal creditLimit = customer.getTotalCredit();

                // Check if new total balance would exceed credit limit
                BigDecimal newBalance = currentBalance.add(orderAmount);
                if (newBalance.compareTo(creditLimit) > 0) {
                    return ResponseEntity.badRequest().body("Total credit balance would exceed credit limit");
                }

                // Update customer's credit balance
                customer.setCreditBalance(newBalance);
                customerService.updateCustomer(customer, adminId);
                order.setStatus("COMPLETED"); // Mark credit orders as completed
            }

            Order savedOrder = orderService.createOrder(order,
                    orderRequest.getMenuItemIds(),
                    orderRequest.getQuantities());

            return ResponseEntity.ok(savedOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrders(@RequestHeader("Admin-ID") String adminId) {
        try {
            List<Order> orders = orderService.getOrdersByAdminId(adminId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable String id,
                                         @RequestHeader("Admin-ID") String adminId) {
        try {
            Order order = orderService.getOrderById(id);
            if (order.getAdminId().equals(adminId)) {
                return ResponseEntity.ok(order);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable String id,
                                        @RequestHeader("Admin-ID") String adminId) {
        try {
            Order order = orderService.getOrderById(id);
            if (!order.getAdminId().equals(adminId)) {
                return ResponseEntity.notFound().build();
            }
            orderService.deleteOrder(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable String id,
                                        @RequestBody Order updatedOrder,
                                        @RequestHeader("Admin-ID") String adminId) {
        try {
            Order existingOrder = orderService.getOrderById(id);
            if (!existingOrder.getAdminId().equals(adminId)) {
                return ResponseEntity.notFound().build();
            }

            // Update only allowed fields
            existingOrder.setStatus(updatedOrder.getStatus());
            existingOrder.setPaymentMethod(updatedOrder.getPaymentMethod());
            existingOrder.setNotes(updatedOrder.getNotes());

            Order savedOrder = orderService.updateOrder(existingOrder);
            return ResponseEntity.ok(savedOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @Data
    static class OrderRequest {
        private String customerId;
        private List<String> menuItemIds;
        private List<Integer> quantities;
        private String paymentMethod;
        private String notes;
        private BigDecimal tax;
        private BigDecimal totalAmount;

        // Explicit getters to ensure Lombok generates them
        public String getCustomerId() { return customerId; }
        public List<String> getMenuItemIds() { return menuItemIds; }
        public List<Integer> getQuantities() { return quantities; }
        public String getPaymentMethod() { return paymentMethod; }
        public String getNotes() { return notes; }
        public BigDecimal getTax() { return tax; }
        public BigDecimal getTotalAmount() { return totalAmount; }
    }
}