package com.restaurant.creditmanagement.config;

import com.restaurant.creditmanagement.model.Admin;
import com.restaurant.creditmanagement.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AdminInitializer {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeAdmin() {
        // Ensure that the default admin is created only if it doesn't exist already
        try {
            Optional<Admin> existingAdminOpt = adminRepository.findByUsername("admin");
            if (!existingAdminOpt.isPresent()) {
                Admin admin = new Admin();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123")); // Ensure password is encoded
                admin.setName("System Administrator");
                admin.setActive(true);

                adminRepository.save(admin); // Save the admin to the database
                System.out.println("Default admin created successfully!");
            } else {
                System.out.println("Default admin already exists.");
            }
        } catch (Exception e) {
            System.err.println("Error initializing admin: " + e.getMessage());
            e.printStackTrace();
            // Don't fail the application startup if admin initialization fails
            System.err.println("Continuing startup without admin initialization...");
        }
    }
}
