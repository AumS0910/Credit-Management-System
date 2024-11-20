package com.restaurant.creditmanagement.controller;

import com.restaurant.creditmanagement.model.Admin;
import com.restaurant.creditmanagement.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class RegisterController {

    @Autowired
    private AdminRepository adminRepository;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("admin")) {
            model.addAttribute("admin", new Admin());
        }
        return "register";
    }

    @PostMapping("/register")
    public String registerAdmin(@ModelAttribute Admin admin, RedirectAttributes redirectAttributes) {
        try {
            // Check if username already exists
            Admin existingAdmin = adminRepository.findByUsername(admin.getUsername());
            if (existingAdmin != null) {
                redirectAttributes.addFlashAttribute("error", "Username already exists!");
                redirectAttributes.addFlashAttribute("admin", admin);
                return "redirect:/register";
            }
            
            // Save the new admin
            adminRepository.save(admin);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed! Please try again.");
            redirectAttributes.addFlashAttribute("admin", admin);
            return "redirect:/register";
        }
    }
}
