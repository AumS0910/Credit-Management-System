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

    public Optional<Admin> findByUsername(String username) {
        return Optional.ofNullable(adminRepository.findByUsername(username));
    }

    public Admin saveAdmin(Admin admin) {
        return adminRepository.save(admin);
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
