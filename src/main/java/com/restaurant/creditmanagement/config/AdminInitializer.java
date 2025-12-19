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
        // Debug MongoDB configuration
        String mongoUri = System.getProperty("spring.data.mongodb.uri");
        String envVar = System.getenv("MONGODB_URI");

        System.out.println("🔍 DEBUG: spring.data.mongodb.uri property = " + mongoUri);
        System.out.println("🔍 DEBUG: MONGODB_URI environment variable = " + envVar);

        if (mongoUri == null || mongoUri.isEmpty() || mongoUri.equals("${MONGODB_URI}")) {
            System.err.println("❌ ERROR: MongoDB URI not properly configured!");
            System.err.println("💡 Set MONGODB_URI environment variable in Render");
            return; // Don't try to initialize admin if DB is not configured
        }

        try {
            System.out.println("Initializing default admin user...");

            Optional<Admin> existingAdminOpt = adminRepository.findByUsername("admin");
            if (!existingAdminOpt.isPresent()) {
                Admin admin = new Admin();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setName("System Administrator");
                admin.setActive(true);
                admin.setCreatedAt(new java.util.Date());

                adminRepository.save(admin);
                System.out.println("✅ Default admin created successfully!");
            } else {
                System.out.println("ℹ️  Default admin already exists.");
            }
        } catch (Exception e) {
            System.err.println("⚠️  Admin initialization failed: " + e.getMessage());
            System.err.println("📝 Application will continue without admin initialization.");
            System.err.println("💡 You can create admin users through the API endpoints.");
        }
    }
}
