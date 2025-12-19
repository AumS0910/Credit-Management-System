package com.restaurant.creditmanagement.service;

import com.restaurant.creditmanagement.model.Customer;
import com.restaurant.creditmanagement.model.Order;
import com.restaurant.creditmanagement.repository.CustomerRepository;
import com.restaurant.creditmanagement.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;



    public long getTotalOrders(String adminId) {
        return orderRepository.countByAdminId(adminId);
    }



    public List<Customer> getAllCustomers(String adminId) {
        return customerRepository.findByAdminId(adminId);
    }

    public List<Customer> getTopCustomers(String adminId) {
        return customerRepository.findTop5ByAdminIdOrderByCreditBalanceDesc(adminId);
    }

    public BigDecimal getTotalOutstandingCredit(String adminId) {
        List<Customer> customers = customerRepository.findByAdminId(adminId);
        return customers.stream()
                .map(Customer::getCreditBalance)
                .filter(balance -> balance.compareTo(BigDecimal.ZERO) > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public long getTotalCustomers(String adminId) {
        return customerRepository.countByAdminId(adminId);
    }

    public List<Order> getRecentOrders(String adminId) {
        return orderRepository.findTop5ByAdminIdOrderByCreatedAtDesc(adminId);
    }
}
