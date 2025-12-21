package com.restaurant.creditmanagement.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Value("${twilio.account.sid:}")
    private String accountSid;

    @Value("${twilio.auth.token:}")
    private String authToken;

    @Value("${twilio.phone.number:}")
    private String twilioPhoneNumber;

    public void sendOrderStatusSms(String toPhoneNumber, String customerName, String orderId, String status) {
        try {
            // For now, just log the SMS that would be sent
            // In production, integrate with Twilio or another SMS service
            String message = String.format(
                "Hi %s, your order #%s status is now: %s. Thank you for choosing our restaurant!",
                customerName, orderId, status
            );

            System.out.println("SMS would be sent to " + toPhoneNumber + ": " + message);

            // TODO: Integrate with actual SMS service like Twilio
            /*
            Twilio.init(accountSid, authToken);
            Message.creator(
                new PhoneNumber(toPhoneNumber),
                new PhoneNumber(twilioPhoneNumber),
                message
            ).create();
            */

        } catch (Exception e) {
            System.err.println("Failed to send SMS: " + e.getMessage());
        }
    }

    public void sendOrderConfirmationSms(String toPhoneNumber, String customerName, String orderId) {
        try {
            String message = String.format(
                "Hi %s, your order #%s has been confirmed! We'll notify you when it's ready.",
                customerName, orderId
            );

            System.out.println("Order confirmation SMS would be sent to " + toPhoneNumber + ": " + message);

            // TODO: Integrate with actual SMS service

        } catch (Exception e) {
            System.err.println("Failed to send confirmation SMS: " + e.getMessage());
        }
    }
}