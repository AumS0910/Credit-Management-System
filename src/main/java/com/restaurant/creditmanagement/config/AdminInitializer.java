package com.restaurant.creditmanagement.config;

import com.restaurant.creditmanagement.model.Admin;
import com.restaurant.creditmanagement.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Ensure that the default admin is created only if it doesn't exist already
        try {
            Admin existingAdmin = adminRepository.findByUsername("admin");
            if (existingAdmin == null) {
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
