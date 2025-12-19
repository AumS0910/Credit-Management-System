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
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

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

            // Encode password before saving
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));

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

            if (existingAdmin.isPresent() && passwordEncoder.matches(admin.getPassword(), existingAdmin.get().getPassword())) {
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

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        try {
            // Invalidate the session
            session.invalidate();

            // Optionally, you can clear any server-side token or session data here

            return ResponseEntity.ok(Collections.singletonMap("message", "Logout successful"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Logout failed: " + e.getMessage()));
        }
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<?> getAdminSettings(@PathVariable Long id) {
        try {
            Admin admin = adminService.getAdminSettings(id);
            Map<String, Object> response = new HashMap<>();
            response.put("id", admin.getId());
            response.put("name", admin.getName());
            response.put("email", admin.getEmail());
            response.put("restaurantName", admin.getRestaurantName());
            response.put("phoneNumber", admin.getPhoneNumber());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", "Failed to fetch admin settings: " + e.getMessage()));
        }
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<?> updateAdminSettings(
            @PathVariable Long id,
            @RequestBody Admin adminDetails) {
        try {
            Admin updatedAdmin = adminService.updateAdminSettings(id, adminDetails);
            Map<String, Object> response = new HashMap<>();
            response.put("id", updatedAdmin.getId());
            response.put("name", updatedAdmin.getName());
            response.put("email", updatedAdmin.getEmail());
            response.put("restaurantName", updatedAdmin.getRestaurantName());
            response.put("phoneNumber", updatedAdmin.getPhoneNumber());
            response.put("message", "Settings updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", "Failed to update admin settings: " + e.getMessage()));
        }
    }
}