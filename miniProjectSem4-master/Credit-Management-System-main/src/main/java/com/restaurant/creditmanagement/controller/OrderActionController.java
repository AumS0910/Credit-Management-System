package com.restaurant.creditmanagement.controller;

import com.restaurant.creditmanagement.model.Order;
import com.restaurant.creditmanagement.repository.OrderRepository;
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

    @PostMapping("/{id}/start")
    public String startOrder(@PathVariable Long id, 
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

            order.setStatus("APPROVED");
            orderRepository.save(order);
            redirectAttributes.addFlashAttribute("success", "Order started successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to start order: " + e.getMessage());
        }

        return "redirect:/orders";
    }

    @PostMapping("/{id}/complete")
    public String completeOrder(@PathVariable Long id, 
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

            order.setStatus("COMPLETED");
            orderRepository.save(order);
            redirectAttributes.addFlashAttribute("success", "Order completed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to complete order: " + e.getMessage());
        }

        return "redirect:/orders";
    }

    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable Long id, 
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

            order.setStatus("CANCELLED");
            orderRepository.save(order);
            redirectAttributes.addFlashAttribute("success", "Order cancelled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to cancel order: " + e.getMessage());
        }

        return "redirect:/orders";
    }
}
