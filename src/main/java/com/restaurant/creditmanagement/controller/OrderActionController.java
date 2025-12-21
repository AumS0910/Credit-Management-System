package com.restaurant.creditmanagement.controller;

import com.restaurant.creditmanagement.model.Order;
import com.restaurant.creditmanagement.repository.OrderRepository;
import com.restaurant.creditmanagement.service.KafkaProducerService;
import com.restaurant.creditmanagement.events.OrderEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/orders")
public class OrderActionController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @PostMapping("/{id}/start")
    public String startOrder(@PathVariable String id,
                            RedirectAttributes redirectAttributes,
                            HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/login";
        }

        try {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            if (!"PENDING".equals(order.getStatus())) {
                throw new IllegalStateException("Order can only be started from PENDING status");
            }

            String previousStatus = order.getStatus();
            order.setStatus("APPROVED");
            Order savedOrder = orderRepository.save(order);

            // Publish order status changed event to Kafka
            try {
                String adminId = session.getAttribute("adminId").toString();
                OrderEvent orderEvent = new OrderEvent("ORDER_STATUS_CHANGED", savedOrder.getId(), adminId);
                orderEvent.setPreviousStatus(previousStatus);
                orderEvent.setNewStatus("APPROVED");
                orderEvent.setCustomerId(savedOrder.getCustomerId());
                orderEvent.setTotalAmount(savedOrder.getTotalAmount());
                orderEvent.setPaymentMethod(savedOrder.getPaymentMethod());
                kafkaProducerService.sendOrderStatusChangedEvent(orderEvent);
            } catch (Exception e) {
                // Log error but don't fail the operation
                System.err.println("Failed to publish order status changed event: " + e.getMessage());
            }

            redirectAttributes.addFlashAttribute("success", "Order started successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to start order: " + e.getMessage());
        }

        return "redirect:/orders";
    }

    @PostMapping("/{id}/complete")
    public String completeOrder(@PathVariable String id,
                               RedirectAttributes redirectAttributes,
                               HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/login";
        }

        try {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            if (!"APPROVED".equals(order.getStatus())) {
                throw new IllegalStateException("Order can only be completed from APPROVED status");
            }

            String previousStatus = order.getStatus();
            order.setStatus("COMPLETED");
            Order savedOrder = orderRepository.save(order);

            // Publish order status changed event to Kafka
            try {
                String adminId = session.getAttribute("adminId").toString();
                OrderEvent orderEvent = new OrderEvent("ORDER_STATUS_CHANGED", savedOrder.getId(), adminId);
                orderEvent.setPreviousStatus(previousStatus);
                orderEvent.setNewStatus("COMPLETED");
                orderEvent.setCustomerId(savedOrder.getCustomerId());
                orderEvent.setTotalAmount(savedOrder.getTotalAmount());
                orderEvent.setPaymentMethod(savedOrder.getPaymentMethod());
                kafkaProducerService.sendOrderStatusChangedEvent(orderEvent);
            } catch (Exception e) {
                // Log error but don't fail the operation
                System.err.println("Failed to publish order status changed event: " + e.getMessage());
            }

            redirectAttributes.addFlashAttribute("success", "Order completed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to complete order: " + e.getMessage());
        }

        return "redirect:/orders";
    }

    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable String id,
                             RedirectAttributes redirectAttributes,
                             HttpSession session) {
        if (session.getAttribute("adminId") == null) {
            return "redirect:/login";
        }

        try {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            if ("COMPLETED".equals(order.getStatus()) || "CANCELLED".equals(order.getStatus())) {
                throw new IllegalStateException("Cannot cancel order in " + order.getStatus() + " status");
            }

            String previousStatus = order.getStatus();
            order.setStatus("CANCELLED");
            Order savedOrder = orderRepository.save(order);

            // Publish order cancelled event to Kafka
            try {
                String adminId = session.getAttribute("adminId").toString();
                OrderEvent orderEvent = new OrderEvent("ORDER_CANCELLED", savedOrder.getId(), adminId);
                orderEvent.setPreviousStatus(previousStatus);
                orderEvent.setNewStatus("CANCELLED");
                orderEvent.setCustomerId(savedOrder.getCustomerId());
                orderEvent.setTotalAmount(savedOrder.getTotalAmount());
                orderEvent.setPaymentMethod(savedOrder.getPaymentMethod());
                kafkaProducerService.sendOrderCancelledEvent(orderEvent);
            } catch (Exception e) {
                // Log error but don't fail the operation
                System.err.println("Failed to publish order cancelled event: " + e.getMessage());
            }

            redirectAttributes.addFlashAttribute("success", "Order cancelled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to cancel order: " + e.getMessage());
        }

        return "redirect:/orders";
    }
}
