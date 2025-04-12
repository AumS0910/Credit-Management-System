package com.restaurant.creditmanagement.config;

import com.restaurant.creditmanagement.model.Admin;
import com.restaurant.creditmanagement.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class AdminInitializer {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        // Ensure that the default admin is created only if it doesn't exist already
        if (adminRepository.findByUsername("admin") == null) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123")); // Ensure password is encoded
            admin.setName("System Administrator");
            admin.setActive(true);

            adminRepository.save(admin); // Save the admin to the database
            System.out.println("Default admin created!");
        }
    }
}
