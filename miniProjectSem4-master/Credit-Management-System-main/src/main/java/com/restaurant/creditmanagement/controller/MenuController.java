package com.restaurant.creditmanagement.controller;

import com.restaurant.creditmanagement.model.MenuItem;
import com.restaurant.creditmanagement.service.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/menu-items")
@CrossOrigin(origins = "http://localhost:3000") // Add CORS support
public class MenuController {

    @Autowired
    private MenuItemService menuItemService;

    @PostMapping("/add")
    public ResponseEntity<?> addMenuItem(@RequestBody MenuItem menuItem) {
        try {
            // Validate adminId exists in request
            if (menuItem.getAdminId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        Collections.singletonMap("error", "Admin ID is required")
                );
            }

            MenuItem savedItem = menuItemService.addMenuItem(menuItem);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Collections.singletonMap("error", "Failed to add menu item: " + e.getMessage())
            );
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> getMenuList(@RequestHeader("Authorization") String authHeader,
                                         @RequestHeader("Admin-ID") String adminId) {
        try {
            // Validate token format
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        Collections.singletonMap("error", "Invalid authorization header")
                );
            }

            // Convert adminId from string to Long
            Long adminIdLong = Long.parseLong(adminId);

            List<MenuItem> menuItems = menuItemService.getAllMenuItems(adminIdLong);
            return ResponseEntity.ok(menuItems != null ? menuItems : Collections.emptyList());
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Collections.singletonMap("error", "Invalid Admin ID format")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Collections.singletonMap("error", "Failed to fetch menu items: " + e.getMessage())
            );
        }
    }

    // Remove the extractAdminIdFromToken method as we're now using the Admin-ID header
}