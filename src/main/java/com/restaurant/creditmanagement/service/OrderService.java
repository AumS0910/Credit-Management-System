package com.restaurant.creditmanagement.service;

import com.restaurant.creditmanagement.model.Order;
import com.restaurant.creditmanagement.model.Customer;

import com.restaurant.creditmanagement.repository.OrderRepository;
import com.restaurant.creditmanagement.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Order createOrder(Order order) {
        // Update customer credit balance
        Customer customer = order.getCustomer();
        if (customer != null) {
            BigDecimal newBalance = customer.getCreditBalance().subtract(order.getTotalAmount());
            if (newBalance.compareTo(BigDecimal.ZERO) >= 0) {
                customer.setCreditBalance(newBalance);
                customerRepository.save(customer);
                return orderRepository.save(order);
            }
            throw new IllegalStateException("Insufficient credit balance");
        }
        throw new IllegalStateException("Customer not found");
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    public List<Order> getRecentOrders() {
        return orderRepository.findAll(
            PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "orderDate"))
        ).getContent();
    }

    public List<Order> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }


}
