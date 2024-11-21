package com.restaurant.creditmanagement.controller;

import com.restaurant.creditmanagement.model.*;
import com.restaurant.creditmanagement.repository.CustomerRepository;
import com.restaurant.creditmanagement.repository.MenuItemRepository;
import com.restaurant.creditmanagement.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @GetMapping
    public String listOrders(Model model, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/login";
        }
        List<Order> orders = orderRepository.findAll();
        model.addAttribute("orders", orders);
        return "orders/list";
    }

    @GetMapping("/new")
    public String showCreateOrderForm(Model model, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/login";
        }
        
        Order order = new Order();
        order.setItems(new ArrayList<>());
        order.getItems().add(new OrderItem()); // Add one empty item by default
        
        model.addAttribute("order", order);
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("menuItems", menuItemRepository.findByAvailableTrue());
        model.addAttribute("paymentMethods", PaymentMethod.values());
        
        return "orders/form";
    }

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> orderData, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            logger.info("Received order data: {}", orderData);

            // Create new order
            Order order = new Order();
            
            // Set customer
            Long customerId = Long.parseLong(orderData.get("customer.id").toString());
            Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
            order.setCustomer(customer);

            // Set payment method
            order.setPaymentMethod(PaymentMethod.valueOf(orderData.get("paymentMethod").toString()));

            // Set notes if present
            if (orderData.get("notes") != null) {
                order.setNotes(orderData.get("notes").toString());
            }

            // Set timestamps
            order.setCreatedAt(LocalDateTime.now());
            order.setOrderDate(LocalDateTime.now());

            // Process order items
            List<Map<String, Object>> items = (List<Map<String, Object>>) orderData.get("items");
            List<OrderItem> orderItems = new ArrayList<>();

            for (Map<String, Object> itemData : items) {
                if (itemData.get("menuItem.id") != null && !itemData.get("menuItem.id").toString().isEmpty()) {
                    OrderItem item = new OrderItem();
                    
                    // Set menu item
                    Long menuItemId = Long.parseLong(itemData.get("menuItem.id").toString());
                    MenuItem menuItem = menuItemRepository.findById(menuItemId)
                        .orElseThrow(() -> new RuntimeException("Menu item not found: " + menuItemId));
                    item.setMenuItem(menuItem);
                    
                    // Set quantity
                    item.setQuantity(Integer.parseInt(itemData.get("quantity").toString()));
                    
                    // Set price from menu item
                    item.setPrice(menuItem.getPrice());
                    
                    // Calculate subtotal
                    item.calculateSubtotal();
                    
                    // Set order reference
                    item.setOrder(order);
                    
                    orderItems.add(item);
                }
            }

            order.setItems(orderItems);

            // Set total amount and tax
            BigDecimal totalAmount = new BigDecimal(orderData.get("totalAmount").toString());
            BigDecimal tax = new BigDecimal(orderData.get("tax").toString());
            order.setTotalAmount(totalAmount);
            order.setTax(tax);

            // Save the order
            Order savedOrder = orderRepository.save(order);
            logger.info("Order saved successfully with ID: {}", savedOrder.getId());

            // Update customer's credit balance
            if (order.getPaymentMethod() == PaymentMethod.CREDIT) {
                BigDecimal newBalance = customer.getCreditBalance().add(order.getTotalAmount());
                customer.setCreditBalance(newBalance);
                customerRepository.save(customer);
                logger.info("Updated customer credit balance to: {}", newBalance);
            }

            return ResponseEntity.ok().body(Map.of(
                "message", "Order created successfully",
                "orderId", savedOrder.getId()
            ));

        } catch (Exception e) {
            logger.error("Error creating order", e);
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error creating order: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditOrderForm(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/login";
        }

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        model.addAttribute("order", order);
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("menuItems", menuItemRepository.findByAvailableTrue());
        model.addAttribute("paymentMethods", PaymentMethod.values());

        return "orders/form";
    }

    @PostMapping("/edit/{id}")
    @ResponseBody
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @RequestBody Map<String, Object> orderData, 
                                       HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            Order existingOrder = orderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            // Update customer
            Long customerId = Long.parseLong(orderData.get("customer.id").toString());
            Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
            existingOrder.setCustomer(customer);

            // Update payment method
            existingOrder.setPaymentMethod(PaymentMethod.valueOf(orderData.get("paymentMethod").toString()));

            // Update notes
            if (orderData.get("notes") != null) {
                existingOrder.setNotes(orderData.get("notes").toString());
            }

            // Update order items
            List<Map<String, Object>> items = (List<Map<String, Object>>) orderData.get("items");
            List<OrderItem> orderItems = new ArrayList<>();

            for (Map<String, Object> itemData : items) {
                if (itemData.get("menuItem.id") != null && !itemData.get("menuItem.id").toString().isEmpty()) {
                    OrderItem item = new OrderItem();
                    
                    // Set menu item
                    Long menuItemId = Long.parseLong(itemData.get("menuItem.id").toString());
                    MenuItem menuItem = menuItemRepository.findById(menuItemId)
                        .orElseThrow(() -> new RuntimeException("Menu item not found: " + menuItemId));
                    item.setMenuItem(menuItem);
                    
                    // Set quantity
                    item.setQuantity(Integer.parseInt(itemData.get("quantity").toString()));
                    
                    // Set price from menu item
                    item.setPrice(menuItem.getPrice());
                    
                    // Calculate subtotal
                    item.calculateSubtotal();
                    
                    // Set order reference
                    item.setOrder(existingOrder);
                    
                    orderItems.add(item);
                }
            }

            existingOrder.getItems().clear();
            existingOrder.getItems().addAll(orderItems);

            // Update total amount and tax
            BigDecimal totalAmount = new BigDecimal(orderData.get("totalAmount").toString());
            BigDecimal tax = new BigDecimal(orderData.get("tax").toString());

            // If this is a credit order, adjust the customer's credit balance
            if (existingOrder.getPaymentMethod() == PaymentMethod.CREDIT) {
                // Remove the old amount from credit balance
                customer.setCreditBalance(customer.getCreditBalance().subtract(existingOrder.getTotalAmount()));
            }

            existingOrder.setTotalAmount(totalAmount);
            existingOrder.setTax(tax);

            // Save the updated order
            Order savedOrder = orderRepository.save(existingOrder);

            // If the updated order is credit, add the new amount to credit balance
            if (existingOrder.getPaymentMethod() == PaymentMethod.CREDIT) {
                customer.setCreditBalance(customer.getCreditBalance().add(totalAmount));
                customerRepository.save(customer);
                logger.info("Updated customer credit balance after order edit to: {}", customer.getCreditBalance());
            }

            return ResponseEntity.ok().body(Map.of(
                "message", "Order updated successfully",
                "orderId", savedOrder.getId()
            ));

        } catch (Exception e) {
            logger.error("Error updating order", e);
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error updating order: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/login";
        }

        try {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            // If this was a credit order, subtract the amount from customer's credit balance
            if (order.getPaymentMethod() == PaymentMethod.CREDIT) {
                Customer customer = order.getCustomer();
                customer.setCreditBalance(customer.getCreditBalance().subtract(order.getTotalAmount()));
                customerRepository.save(customer);
                logger.info("Updated customer credit balance after order deletion to: {}", customer.getCreditBalance());
            }

            orderRepository.delete(order);
            redirectAttributes.addFlashAttribute("success", "Order deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting order", e);
            redirectAttributes.addFlashAttribute("error", "Error deleting order: " + e.getMessage());
        }

        return "redirect:/orders";
    }
}
