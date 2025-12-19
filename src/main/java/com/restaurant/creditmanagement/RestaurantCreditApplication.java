package com.restaurant.creditmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.restaurant.creditmanagement")
public class RestaurantCreditApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestaurantCreditApplication.class, args);
    }
}
