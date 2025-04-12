package com.restaurant.creditmanagement.controller;

import com.restaurant.creditmanagement.model.Admin;
import com.restaurant.creditmanagement.repository.AdminRepository;
import com.restaurant.creditmanagement.repository.CustomerRepository;
import com.restaurant.creditmanagement.service.AdminService;
import com.restaurant.creditmanagement.service.CustomerService;
import com.restaurant.creditmanagement.service.DashboardService;
import com.restaurant.creditmanagement.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.*;

@RestController
@RequestMapping("/api")
public class AdminController {
    @Autowired
    private AdminService adminService;

    // Remove the GET /register endpoint (not needed for SPA)
    // @GetMapping("/register") <- Remove this

    @PostMapping("/register")
    public ResponseEntity<?> registerAdmin(@RequestBody Admin admin) {
        try {
            // Validate all fields
            if (admin.getName() == null || admin.getName().trim().isEmpty() ||
                admin.getUsername() == null || admin.getUsername().trim().isEmpty() ||
                admin.getPassword() == null || admin.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                    Collections.singletonMap("error", "All fields are required")
                );
            }

            // Check username availability
            Optional<Admin> existingAdmin = adminService.findByUsername(admin.getUsername());
            if (existingAdmin.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Collections.singletonMap("error", "Username already exists")
                );
            }

            // Set admin properties
            admin.setActive(true);
            admin.setCreatedAt(new Date());
            
            // Save and return response
            Admin savedAdmin = adminService.registerAdmin(admin);
            Map<String, Object> response = new HashMap<>();
            response.put("id", savedAdmin.getId());
            response.put("username", savedAdmin.getUsername());
            response.put("message", "Registration successful");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Collections.singletonMap("error", "Registration failed: " + e.getMessage())
            );
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("admin", new Admin());
        return "login";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Admin admin) {
        try {
            Optional<Admin> existingAdmin = adminService.findByUsername(admin.getUsername());
            
            if (existingAdmin.isPresent() && existingAdmin.get().getPassword().equals(admin.getPassword())) {
                Admin loggedInAdmin = existingAdmin.get();
                
                // Generate token (in production use JWT)
                String token = UUID.randomUUID().toString();
                
                Map<String, Object> response = new HashMap<>();
                response.put("id", loggedInAdmin.getId());
                response.put("username", loggedInAdmin.getUsername());
                response.put("token", token); // Add token to response
                response.put("message", "Login successful");
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Invalid credentials"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", "Server error"));
        }
    }
}
