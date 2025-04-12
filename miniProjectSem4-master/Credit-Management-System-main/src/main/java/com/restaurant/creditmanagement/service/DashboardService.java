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



    public long getTotalOrders(Long adminId) {
        return orderRepository.countByAdminId(adminId);
    }



    public List<Customer> getAllCustomers(Long adminId) {
        return customerRepository.findByAdminId(adminId);
    }

    public List<Customer> getTopCustomers(Long adminId) {
        return customerRepository.findTop5ByAdminIdOrderByCreditBalanceDesc(adminId);
    }

    public BigDecimal getTotalOutstandingCredit(Long adminId) {
        return customerRepository.getTotalOutstandingCreditByAdminId(adminId);
    }

    public long getTotalCustomers(Long adminId) {
        return customerRepository.countCustomersByAdminId(adminId);
    }

    public List<Order> getRecentOrders(Long adminId) {
        return orderRepository.findTop5ByAdminIdOrderByCreatedAtDesc(adminId);
    }
}
