package com.restaurant.creditmanagement.service;

import com.restaurant.creditmanagement.model.Admin;
import com.restaurant.creditmanagement.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public Admin registerAdmin(Admin admin) {
        // Check if username already exists
        Admin existingAdmin = adminRepository.findByUsername(admin.getUsername());
        if (existingAdmin != null) {
            throw new RuntimeException("Username already exists");
        }

        // Basic validation
        if (admin.getUsername() == null || admin.getUsername().trim().isEmpty()) {
            throw new RuntimeException("Username cannot be empty");
        }
        if (admin.getPassword() == null || admin.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Password cannot be empty");
        }
        if (admin.getName() == null || admin.getName().trim().isEmpty()) {
            throw new RuntimeException("Name cannot be empty");
        }

        // Set default values
        admin.setActive(true);
        admin.setCreatedAt(new java.util.Date());

        // Save the new admin
        try {
            return adminRepository.save(admin);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save admin: " + e.getMessage());
        }
    }

    public Admin authenticate(String username, String password) {
        Admin admin = adminRepository.findByUsername(username);
        if (admin != null && admin.getPassword().equals(password)) {
            return admin;
        }
        return null;
    }

    public Optional<Admin> findByUsername(String username) {
        return Optional.ofNullable(adminRepository.findByUsername(username));
    }

    public boolean validateCredentials(String username, String password) {
        Optional<Admin> adminOpt = Optional.ofNullable(adminRepository.findByUsername(username));
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            // In a real application, you should use proper password hashing
            return admin.getPassword().equals(password) && admin.getActive();
        }
        return false;
    }

    public Optional<Admin> getAdminById(Long id) {
        return adminRepository.findById(id);
    }

    public void deactivateAdmin(Long id) {
        Optional<Admin> adminOpt = adminRepository.findById(id);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            admin.setActive(false);
            adminRepository.save(admin);
        }
    }

    public void activateAdmin(Long id) {
        Optional<Admin> adminOpt = adminRepository.findById(id);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            admin.setActive(true);
            adminRepository.save(admin);
        }
    }
}
