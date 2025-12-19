package com.restaurant.creditmanagement.config;

import com.restaurant.creditmanagement.model.Admin;
import com.restaurant.creditmanagement.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AdminInitializer {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeAdmin() {
        System.out.println("🔄 AdminInitializer: Starting admin initialization...");
        try {
            long adminCount = adminRepository.count();
            System.out.println("🔄 AdminInitializer: Current admin count: " + adminCount);

            if (adminRepository.findByUsername("admin").isEmpty()) {
                System.out.println("🔄 AdminInitializer: No admin found, creating default admin...");
                Admin admin = new Admin();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setName("System Administrator");
                admin.setActive(true);
                admin.setCreatedAt(new Date());
                Admin savedAdmin = adminRepository.save(admin);
                System.out.println("✅ AdminInitializer: Default admin created with ID: " + savedAdmin.getId());
            } else {
                System.out.println("✅ AdminInitializer: Default admin already exists");
            }
        } catch (Exception e) {
            System.err.println("❌ AdminInitializer: Failed to initialize admin: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
