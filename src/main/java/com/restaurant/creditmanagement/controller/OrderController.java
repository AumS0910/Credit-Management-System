package com.restaurant.creditmanagement.controller;

import com.restaurant.creditmanagement.model.Customer;
import com.restaurant.creditmanagement.model.Order;
import com.restaurant.creditmanagement.model.OrderItem;
import com.restaurant.creditmanagement.model.PaymentMethod;
import com.restaurant.creditmanagement.repository.CustomerRepository;
import com.restaurant.creditmanagement.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
        
        List<Customer> customers = customerRepository.findAll();
        model.addAttribute("customers", customers);
        model.addAttribute("order", new Order());
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("isEdit", false);
        return "orders/create-order";
    }

    @PostMapping("/new")
    public String createOrder(@RequestParam Map<String, String> allParams,
                            RedirectAttributes redirectAttributes,
                            HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/login";
        }
        
        try {
            logger.info("Received parameters: {}", allParams);
            
            // Extract and validate customerId
            Long customerId = Long.parseLong(allParams.get("customerId"));
            Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

            // Extract and validate totalAmount
            BigDecimal totalAmount = new BigDecimal(allParams.get("orderTotalAmount"));

            // Extract and validate paymentMethod
            PaymentMethod paymentMethod = PaymentMethod.valueOf(allParams.get("orderPaymentMethod"));

            // Get notes if present
            String notes = allParams.get("orderNotes");

            // Validate credit limit for CREDIT payment method
            if (paymentMethod == PaymentMethod.CREDIT) {
                BigDecimal newBalance = customer.getCreditBalance().add(totalAmount);
                if (newBalance.compareTo(customer.getTotalCredit()) > 0) {
                    redirectAttributes.addFlashAttribute("error", 
                        "Order exceeds customer's credit limit. Available credit: ₹" + 
                        customer.getTotalCredit().subtract(customer.getCreditBalance()));
                    return "redirect:/orders/new";
                }
            }

            Order order = new Order();
            order.setCustomer(customer);
            order.setTotalAmount(totalAmount);
            order.setPaymentMethod(paymentMethod);
            order.setNotes(notes);
            order.setOrderDate(LocalDateTime.now());

            // Process order items
            List<OrderItem> items = new ArrayList<>();
            int i = 0;
            while (allParams.containsKey("orderItemName[" + i + "]")) {
                String itemName = allParams.get("orderItemName[" + i + "]");
                int quantity = Integer.parseInt(allParams.get("orderItemQuantity[" + i + "]"));
                BigDecimal price = new BigDecimal(allParams.get("orderItemPrice[" + i + "]"));
                BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity));

                OrderItem item = new OrderItem();
                item.setName(itemName);
                item.setQuantity(quantity);
                item.setPrice(price);
                item.setSubtotal(subtotal);
                item.setOrder(order);
                items.add(item);
                i++;
            }
            order.setItems(items);

            // Save the order
            orderRepository.save(order);

            // Update customer credit balance if payment method is CREDIT
            if (paymentMethod == PaymentMethod.CREDIT) {
                customer.setCreditBalance(customer.getCreditBalance().add(totalAmount));
                customerRepository.save(customer);
            }

            redirectAttributes.addFlashAttribute("success", "Order created successfully!");
            return "redirect:/orders";
        } catch (Exception e) {
            logger.error("Error creating order: ", e);
            redirectAttributes.addFlashAttribute("error", "Error creating order: " + e.getMessage());
            return "redirect:/orders/new";
        }
    }

    @GetMapping("/view/{id}")
    public String viewOrder(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/login";
        }
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        model.addAttribute("order", order);
        return "orders/view";
    }

    @GetMapping("/edit/{id}")
    public String showEditOrderForm(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/login";
        }
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        List<Customer> customers = customerRepository.findAll();
        model.addAttribute("customers", customers);
        model.addAttribute("order", order);
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("isEdit", true);
        return "orders/create-order";
    }

    @PostMapping("/edit/{id}")
    public String updateOrder(@PathVariable Long id,
                            @RequestParam Map<String, String> allParams,
                            RedirectAttributes redirectAttributes,
                            HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/login";
        }
        
        try {
            Order existingOrder = orderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            // Extract and validate customerId
            Long customerId = Long.parseLong(allParams.get("customerId"));
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            // Extract and validate totalAmount
            BigDecimal totalAmount = new BigDecimal(allParams.get("orderTotalAmount"));

            // Extract and validate paymentMethod
            PaymentMethod paymentMethod = PaymentMethod.valueOf(allParams.get("orderPaymentMethod"));

            // Get notes if present
            String notes = allParams.get("orderNotes");

            // If payment method is changing to CREDIT, validate credit limit
            if (paymentMethod == PaymentMethod.CREDIT && existingOrder.getPaymentMethod() != PaymentMethod.CREDIT) {
                BigDecimal newBalance = customer.getCreditBalance().add(totalAmount);
                if (newBalance.compareTo(customer.getTotalCredit()) > 0) {
                    redirectAttributes.addFlashAttribute("error", 
                        "Order exceeds customer's credit limit. Available credit: ₹" + 
                        customer.getTotalCredit().subtract(customer.getCreditBalance()));
                    return "redirect:/orders/edit/" + id;
                }
            }

            // Update order details
            existingOrder.setCustomer(customer);
            existingOrder.setTotalAmount(totalAmount);
            existingOrder.setPaymentMethod(paymentMethod);
            existingOrder.setNotes(notes);

            // Clear existing items and add updated ones
            existingOrder.getItems().clear();
            List<OrderItem> items = new ArrayList<>();
            int i = 0;
            while (allParams.containsKey("orderItemName[" + i + "]")) {
                String itemName = allParams.get("orderItemName[" + i + "]");
                int quantity = Integer.parseInt(allParams.get("orderItemQuantity[" + i + "]"));
                BigDecimal price = new BigDecimal(allParams.get("orderItemPrice[" + i + "]"));
                BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity));

                OrderItem item = new OrderItem();
                item.setName(itemName);
                item.setQuantity(quantity);
                item.setPrice(price);
                item.setSubtotal(subtotal);
                item.setOrder(existingOrder);
                items.add(item);
                i++;
            }
            existingOrder.setItems(items);

            orderRepository.save(existingOrder);
            redirectAttributes.addFlashAttribute("success", "Order updated successfully!");
            return "redirect:/orders";
        } catch (Exception e) {
            logger.error("Error updating order: ", e);
            redirectAttributes.addFlashAttribute("error", "Error updating order: " + e.getMessage());
            return "redirect:/orders/edit/" + id;
        }
    }
}
