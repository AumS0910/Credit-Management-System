package com.restaurant.creditmanagement.controller;

import com.restaurant.creditmanagement.model.MenuItem;
import com.restaurant.creditmanagement.service.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/menu-items")  // Update base path
@CrossOrigin(origins = {"http://localhost:3000", "https://credit-management-system.vercel.app"})
public class MenuController {
    
    @Value("${pexels.api.key}")
    private String pexelsApiKey;

    @Autowired
    private MenuItemService menuItemService;

    @PostMapping("/add")  // This will now map to /api/menu-items/add
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

            List<MenuItem> menuItems = menuItemService.getAllMenuItems(adminId);
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

    @GetMapping("/pexels/food-image")
    public ResponseEntity<?> getFoodImage(@RequestParam String name) {
        try {
            String pexelsUrl = "https://api.pexels.com/v1/search?query=" + 
                              URLEncoder.encode(name + " food", "UTF-8") +
                              "&per_page=1";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", pexelsApiKey);
            
            ResponseEntity<Map> response = new RestTemplate().exchange(
                pexelsUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
            );

            if (response.getBody() != null && 
                response.getBody().containsKey("photos") && 
                !((List)response.getBody().get("photos")).isEmpty()) {
                
                Map<String, Object> photo = (Map<String, Object>)((List)response.getBody().get("photos")).get(0);
                Map<String, String> src = (Map<String, String>)photo.get("src");
                String imageUrl = src.get("medium");
                
                return ResponseEntity.ok(Collections.singletonMap("url", imageUrl));
            }

            return ResponseEntity.ok(Collections.singletonMap("url", 
                "https://via.placeholder.com/400x300?text=No+Image+Found"));
                
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", "Failed to fetch image: " + e.getMessage()));
        }
    }

    // Update the endpoint to match frontend call
    @GetMapping  // Changed from "/list" to root path
    public ResponseEntity<?> getMenuItems(@RequestHeader("Authorization") String authHeader,
                                        @RequestHeader("Admin-ID") String adminId) {
        try {
            // Validate token format
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        Collections.singletonMap("error", "Invalid authorization header")
                );
            }

            List<MenuItem> menuItems = menuItemService.getAllMenuItems(adminId);
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

    // Get specific menu item
    @GetMapping("/{id}")
    public ResponseEntity<?> getMenuItem(@PathVariable String id,
                                       @RequestHeader("Admin-ID") String adminId) {
        try {
            MenuItem menuItem = menuItemService.getMenuItemById(id);
            return menuItem != null ?
                   ResponseEntity.ok(menuItem) :
                   ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Collections.singletonMap("error", "Failed to fetch menu item: " + e.getMessage())
            );
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMenuItem(
            @PathVariable String id,
            @RequestBody MenuItem menuItem,
            @RequestHeader("Admin-ID") String adminId) {
        try {
            menuItem.setId(id);
            menuItem.setAdminId(adminId);

            MenuItem updatedItem = menuItemService.updateMenuItem(menuItem);
            return ResponseEntity.ok(updatedItem);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", "Failed to update menu item: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMenuItem(
            @PathVariable String id,
            @RequestHeader("Admin-ID") String adminId) {
        try {
            menuItemService.deleteMenuItem(id, adminId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", "Failed to delete menu item: " + e.getMessage()));
        }
    }
}