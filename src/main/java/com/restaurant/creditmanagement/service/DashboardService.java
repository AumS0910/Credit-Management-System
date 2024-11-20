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

    public long getTotalCustomers() {
        return customerRepository.countCustomers();
    }

    public long getTotalOrders() {
        return orderRepository.count();
    }

    public BigDecimal getTotalOutstandingCredit() {
        return customerRepository.getTotalOutstandingCredit();
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public List<Customer> getTopCustomers() {
        return customerRepository.findTopCustomersByCredit();
    }

    public List<Order> getRecentOrders() {
        return orderRepository.findAll(
            PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "orderDate"))
        ).getContent();
    }
}
