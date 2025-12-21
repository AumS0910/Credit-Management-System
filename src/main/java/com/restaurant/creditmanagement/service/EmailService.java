package com.restaurant.creditmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String emailUsername;

    public void sendOrderStatusEmail(String toEmail, String customerName, String orderId, String status) {
        if (mailSender == null || emailUsername.isEmpty()) {
            System.out.println("Email service not configured - skipping email to: " + toEmail);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Order Status Update - " + orderId);

            String body = String.format(
                "Dear %s,\n\n" +
                "Your order #%s status has been updated to: %s\n\n" +
                "Thank you for choosing our restaurant!\n\n" +
                "Best regards,\n" +
                "Restaurant Management Team",
                customerName, orderId, status
            );

            message.setText(body);
            mailSender.send(message);

            System.out.println("Email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    public void sendOrderConfirmationEmail(String toEmail, String customerName, String orderId, double totalAmount) {
        if (mailSender == null || emailUsername.isEmpty()) {
            System.out.println("Email service not configured - skipping confirmation email to: " + toEmail);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Order Confirmation - " + orderId);

            String body = String.format(
                "Dear %s,\n\n" +
                "Thank you for your order! Your order #%s has been confirmed.\n\n" +
                "Order Total: $%.2f\n\n" +
                "We will notify you when your order is ready for pickup/delivery.\n\n" +
                "Best regards,\n" +
                "Restaurant Management Team",
                customerName, orderId, totalAmount
            );

            message.setText(body);
            mailSender.send(message);

            System.out.println("Order confirmation email sent to: " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send confirmation email: " + e.getMessage());
        }
    }
}