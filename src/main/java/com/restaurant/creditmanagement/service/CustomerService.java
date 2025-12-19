package com.restaurant.creditmanagement.service;

import com.restaurant.creditmanagement.model.Customer;
import com.restaurant.creditmanagement.model.Transaction;
import com.restaurant.creditmanagement.model.TransactionType;
import com.restaurant.creditmanagement.repository.CustomerRepository;
import com.restaurant.creditmanagement.repository.TransactionRepository;
import com.restaurant.creditmanagement.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CustomerService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerRepository customerRepository;  // Remove static

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private OrderRepository orderRepository;

    public void deleteCustomer(String id) {
        Optional<Customer> customerOpt = customerRepository.findById(id);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();

            if (customer.getCreditBalance().compareTo(BigDecimal.ZERO) > 0) {
                throw new RuntimeException("Cannot delete customer with outstanding balance");
            }

            customerRepository.delete(customer);
        }
    }

    public List<Customer> getAllCustomers(String adminId) {
        return customerRepository.findByAdminId(adminId);
    }

    public Customer createCustomer(Customer customer, String adminId) {
        customer.setAdminId(adminId);
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Customer customer, String adminId) {
        if (!customerRepository.existsById(customer.getId())) {
            throw new RuntimeException("Customer not found");
        }
        customer.setAdminId(adminId);
        return customerRepository.save(customer);
    }

    public List<Customer> searchCustomers(String query, String adminId) {
        return customerRepository.searchByName(query, adminId);
    }

    public Optional<Customer> getCustomerById(String id, String adminId) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isPresent() && customer.get().getAdminId().equals(adminId)) {
            return customer;
        }
        return Optional.empty();
    }



    // Remove the old settleBalance method that uses setCustomer
    
    public Customer settleBalance(String customerId, String adminId, Transaction settlement) {
        Optional<Customer> customerOpt = getCustomerById(customerId, adminId);
        if (!customerOpt.isPresent()) {
            throw new RuntimeException("Customer not found");
        }

        Customer customer = customerOpt.get();
        BigDecimal settlementAmount = settlement.getAmount();

        if (settlementAmount.compareTo(BigDecimal.ZERO) <= 0 ||
            settlementAmount.compareTo(customer.getCreditBalance()) > 0) {
            throw new RuntimeException("Invalid settlement amount");
        }

        // Set transaction details
        settlement.setCustomerId(customerId);
        settlement.setAdminId(adminId);
        settlement.setType(TransactionType.SETTLEMENT);
        settlement.setStatus("COMPLETED");
        settlement.setTransactionDate(LocalDateTime.now());

        // Update customer balance
        customer.setCreditBalance(customer.getCreditBalance().subtract(settlementAmount));

        // Save both transaction and customer
        transactionRepository.save(settlement);
        return customerRepository.save(customer);
    }

    public long countCustomersByAdminId(String adminId) {
        return customerRepository.countByAdminId(adminId);
    }

    public List<Map<String, Object>> getLoyaltyDistribution(String adminId) {
        List<Map<String, Object>> distribution = new ArrayList<>();
        List<Customer> customers = customerRepository.findByAdminId(adminId);

        Map<String, Integer> loyaltyCount = new HashMap<>();

        for (Customer customer : customers) {
            Integer orderCount = orderRepository.countByCustomerId(customer.getId());
            String category = getLoyaltyCategory(orderCount != null ? orderCount : 0);
            loyaltyCount.merge(category, 1, Integer::sum);
        }

        loyaltyCount.forEach((category, count) -> {
            Map<String, Object> categoryData = new HashMap<>();
            categoryData.put("category", category);
            categoryData.put("count", count);
            distribution.add(categoryData);
        });

        return distribution;
    }

    public List<Map<String, Object>> getOrderFrequency(String adminId) {
        List<Map<String, Object>> frequency = new ArrayList<>();
        List<Customer> customers = customerRepository.findByAdminId(adminId);

        Map<String, Integer> frequencyCount = new HashMap<>();

        for (Customer customer : customers) {
            Integer orderCount = orderRepository.countByCustomerId(customer.getId());
            String frequencyCategory = getFrequencyCategory(orderCount != null ? orderCount : 0);
            frequencyCount.merge(frequencyCategory, 1, Integer::sum);
        }

        frequencyCount.forEach((category, count) -> {
            Map<String, Object> categoryData = new HashMap<>();
            categoryData.put("frequency", category);
            categoryData.put("customers", count);
            frequency.add(categoryData);
        });

        return frequency;
    }

    private String getLoyaltyCategory(int orderCount) {
        if (orderCount > 20) return "VIP";
        if (orderCount > 10) return "Regular";
        if (orderCount > 5) return "Occasional";
        return "New";
    }

    private String getFrequencyCategory(int orderCount) {
        if (orderCount > 8) return "Weekly";
        if (orderCount > 4) return "Monthly";
        if (orderCount > 1) return "Occasional";
        return "One-time";
    }
}