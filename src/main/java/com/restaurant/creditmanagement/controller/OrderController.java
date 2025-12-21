package com.restaurant.creditmanagement.controller;

import com.restaurant.creditmanagement.model.Customer;
import com.restaurant.creditmanagement.model.MenuItem;
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
import java.util.ArrayList;
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

    @Autowired
    private WebSocketController webSocketController;

    // Response DTO for orders with customer information
    public static class OrderResponse {
        private String id;
        private CustomerInfo customer;
        private String orderDate;
        private BigDecimal totalAmount;
        private String status;
        private String paymentMethod;
        private String notes;

        public OrderResponse(Order order, Customer customer) {
            this.id = order.getId();
            this.customer = new CustomerInfo(customer.getName());
            this.orderDate = order.getOrderDate().toString();
            this.totalAmount = order.getTotalAmount();
            this.status = order.getStatus();
            this.paymentMethod = order.getPaymentMethod();
            this.notes = order.getNotes();
        }

        // Getters
        public String getId() { return id; }
        public CustomerInfo getCustomer() { return customer; }
        public String getOrderDate() { return orderDate; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public String getStatus() { return status; }
        public String getPaymentMethod() { return paymentMethod; }
        public String getNotes() { return notes; }
    }

    public static class CustomerInfo {
        private String name;

        public CustomerInfo(String name) {
            this.name = name;
        }

        public String getName() { return name; }
    }

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

            // Send real-time order update via WebSocket
            try {
                webSocketController.sendOrderUpdate(savedOrder, "CREATED");
                webSocketController.sendNotification("info",
                    "New order #" + savedOrder.getId() + " received from " + customer.getName(),
                    savedOrder.getId());
            } catch (Exception e) {
                // Log error but don't fail the operation
                System.err.println("Failed to send WebSocket update: " + e.getMessage());
            }

            return ResponseEntity.ok(savedOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(@RequestHeader("Admin-ID") String adminId) {
        try {
            List<Order> orders = orderService.getOrdersByAdminId(adminId);
            List<OrderResponse> orderResponses = new ArrayList<>();

            for (Order order : orders) {
                try {
                    Customer customer = customerService.getCustomerById(order.getCustomerId(), adminId)
                            .orElse(null);
                    if (customer != null) {
                        orderResponses.add(new OrderResponse(order, customer));
                    } else {
                        // Create response with placeholder customer for missing customers
                        Customer placeholderCustomer = new Customer();
                        placeholderCustomer.setName("Unknown Customer");
                        orderResponses.add(new OrderResponse(order, placeholderCustomer));
                    }
                } catch (Exception e) {
                    // If there's an error, still include the order with placeholder customer
                    System.err.println("Error processing order: " + order.getId() + ", " + e.getMessage());
                    Customer placeholderCustomer = new Customer();
                    placeholderCustomer.setName("Unknown Customer");
                    orderResponses.add(new OrderResponse(order, placeholderCustomer));
                }
            }

            return ResponseEntity.ok(orderResponses);
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

            // Send real-time update
            try {
                webSocketController.sendOrderUpdate(savedOrder, "UPDATED");
            } catch (Exception e) {
                System.err.println("Failed to send WebSocket update: " + e.getMessage());
            }

            return ResponseEntity.ok(savedOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<?> startOrder(@PathVariable String id,
                                       @RequestHeader("Admin-ID") String adminId) {
        try {
            Order order = orderService.getOrderById(id);
            if (!order.getAdminId().equals(adminId)) {
                return ResponseEntity.notFound().build();
            }

            if (!"PENDING".equals(order.getStatus())) {
                return ResponseEntity.badRequest().body("Order can only be started from PENDING status");
            }

            order.setStatus("APPROVED");
            Order savedOrder = orderService.updateOrder(order);

            // Send real-time update
            try {
                webSocketController.sendOrderUpdate(savedOrder, "STATUS_CHANGED");
                webSocketController.sendNotification("success",
                    "Order #" + savedOrder.getId() + " has been approved and is being prepared", savedOrder.getId());
            } catch (Exception e) {
                System.err.println("Failed to send WebSocket update: " + e.getMessage());
            }

            return ResponseEntity.ok(savedOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<?> completeOrder(@PathVariable String id,
                                          @RequestHeader("Admin-ID") String adminId) {
        try {
            Order order = orderService.getOrderById(id);
            if (!order.getAdminId().equals(adminId)) {
                return ResponseEntity.notFound().build();
            }

            if (!"APPROVED".equals(order.getStatus())) {
                return ResponseEntity.badRequest().body("Order can only be completed from APPROVED status");
            }

            order.setStatus("COMPLETED");
            Order savedOrder = orderService.updateOrder(order);

            // Send real-time update
            try {
                webSocketController.sendOrderUpdate(savedOrder, "STATUS_CHANGED");
                webSocketController.sendNotification("success",
                    "Order #" + savedOrder.getId() + " has been completed", savedOrder.getId());
            } catch (Exception e) {
                System.err.println("Failed to send WebSocket update: " + e.getMessage());
            }

            return ResponseEntity.ok(savedOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable String id,
                                        @RequestHeader("Admin-ID") String adminId) {
        try {
            Order order = orderService.getOrderById(id);
            if (!order.getAdminId().equals(adminId)) {
                return ResponseEntity.notFound().build();
            }

            if ("COMPLETED".equals(order.getStatus()) || "CANCELLED".equals(order.getStatus())) {
                return ResponseEntity.badRequest().body("Cannot cancel order in " + order.getStatus() + " status");
            }

            order.setStatus("CANCELLED");
            Order savedOrder = orderService.updateOrder(order);

            // Send real-time update
            try {
                webSocketController.sendOrderUpdate(savedOrder, "CANCELLED");
                webSocketController.sendNotification("error",
                    "Order #" + savedOrder.getId() + " has been cancelled", savedOrder.getId());
            } catch (Exception e) {
                System.err.println("Failed to send WebSocket update: " + e.getMessage());
            }

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