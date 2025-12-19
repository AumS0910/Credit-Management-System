package com.restaurant.creditmanagement.config;

import com.restaurant.creditmanagement.model.Admin;
import com.restaurant.creditmanagement.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AdminInitializer {

    @Value("${spring.data.mongodb.uri}")

    private String mongoUri;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeAdmin() {
        System.out.println("🔍 DEBUG: MongoDB URI = " + mongoUri);
        System.out.println("🔍 DEBUG: MONGODB_URI env var = " + System.getenv("MONGODB_URI"));

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
