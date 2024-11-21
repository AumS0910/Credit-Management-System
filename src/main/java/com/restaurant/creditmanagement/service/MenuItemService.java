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
    
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findByAvailableTrue();
    }
    
    public List<MenuItem> getMenuItemsByCategory(String category) {
        return menuItemRepository.findByCategoryOrderByName(category);
    }
    
    public Optional<MenuItem> getMenuItemById(Long id) {
        return menuItemRepository.findById(id);
    }
    
    public MenuItem saveMenuItem(MenuItem menuItem) {
        // Set a default admin ID for now
        if (menuItem.getAdminId() == null) {
            menuItem.setAdminId(1L);
        }
        return menuItemRepository.save(menuItem);
    }
    
    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
    }
    
    public void toggleAvailability(Long id) {
        menuItemRepository.findById(id).ifPresent(item -> {
            item.setAvailable(!item.isAvailable());
            menuItemRepository.save(item);
        });
    }
}
