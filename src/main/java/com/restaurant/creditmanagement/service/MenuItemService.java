package com.restaurant.creditmanagement.service;

import com.restaurant.creditmanagement.model.MenuItem;
import com.restaurant.creditmanagement.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuItemService {
    
    @Autowired
    private MenuItemRepository menuItemRepository;
    
    @Autowired
    private PexelsService pexelsService;
    
    public MenuItem addMenuItem(MenuItem menuItem) {
        if (menuItem.getAdminId() == null) {
            throw new IllegalArgumentException("Admin ID cannot be null");
        }
        
        // Fetch image URL from Pexels based on item name
        String imageUrl = pexelsService.getImageUrlForFood(menuItem.getName());
        menuItem.setImageUrl(imageUrl);
        
        // Set default values
        if (menuItem.getIsSpecial() == null) menuItem.setIsSpecial(false);
        if (menuItem.isAvailable() == null) menuItem.setAvailable(true);
        
        return menuItemRepository.save(menuItem);
    }
    
    public MenuItem updateExistingMenuItem(String id, MenuItem updatedMenuItem) {
        return menuItemRepository.findById(id)
                .map(existingItem -> {
                    existingItem.setName(updatedMenuItem.getName());
                    existingItem.setDescription(updatedMenuItem.getDescription());
                    existingItem.setPrice(updatedMenuItem.getPrice());
                    existingItem.setCategory(updatedMenuItem.getCategory());
                    existingItem.setAvailable(updatedMenuItem.isAvailable());
                    existingItem.setImageUrl(updatedMenuItem.getImageUrl());
                    existingItem.setIsSpecial(updatedMenuItem.getIsSpecial());
                    existingItem.setRating(updatedMenuItem.getRating());
                    return menuItemRepository.save(existingItem);
                })
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + id));
    }

    public MenuItem saveMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    public List<MenuItem> getAllMenuItems(String adminId) {
        return menuItemRepository.findByAdminId(adminId);
    }

    public List<MenuItem> getMenuItemsByAdminId(String adminId) {
        return menuItemRepository.findByAdminId(adminId);
    }

    public MenuItem getMenuItemById(String id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + id));
    }

    public MenuItem updateMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    public void deleteMenuItem(String menuItemId, String adminId) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
            .orElseThrow(() -> new RuntimeException("Menu item not found"));

        if (!menuItem.getAdminId().equals(adminId)) {
            throw new RuntimeException("Unauthorized to delete this menu item");
        }

        menuItemRepository.deleteById(menuItemId);
    }
}
