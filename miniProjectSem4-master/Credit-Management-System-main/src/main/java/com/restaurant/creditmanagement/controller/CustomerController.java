package com.restaurant.creditmanagement.controller;

import com.restaurant.creditmanagement.model.Customer;
import com.restaurant.creditmanagement.model.Transaction;
import com.restaurant.creditmanagement.repository.CustomerRepository;
import com.restaurant.creditmanagement.repository.TransactionRepository;
import com.restaurant.creditmanagement.service.CustomerService;
// Add this import at the top with other imports
import com.restaurant.creditmanagement.model.TransactionType;
import java.time.LocalDateTime;
import com.restaurant.creditmanagement.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/customers")
@CrossOrigin(origins = "http://localhost:3000")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;  // Add this field



    @GetMapping
    public ResponseEntity<List<Customer>> listCustomers(@RequestHeader("Admin-ID") String adminIdStr) {
        try {
            Long adminId = Long.parseLong(adminIdStr);
            List<Customer> customers = customerService.getAllCustomers(adminId);
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCustomer(@RequestBody Customer customer,
                                         @RequestHeader("Admin-ID") String adminIdStr) {
        try {
            Long adminId = Long.parseLong(adminIdStr);
            customer.setCreditBalance(customer.getTotalCredit());
            customer.setActive(true);
            customer.setAdminId(adminId);
            Customer savedCustomer = customerService.createCustomer(customer, adminId);
            return ResponseEntity.ok(savedCustomer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to add customer: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error deleting customer: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Customer>> searchCustomers(@RequestParam String query) {
        try {
            Long adminId = 1L; // TODO: Get this from security context
            List<Customer> customers = customerService.searchCustomers(query, adminId);
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        try {
            Long adminId = 1L; // TODO: Get this from security context
            Optional<Customer> existingCustomerOpt = customerService.getCustomerById(id, adminId);
            if (!existingCustomerOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Customer existingCustomer = existingCustomerOpt.get();
            existingCustomer.setName(customer.getName());
            existingCustomer.setPhone(customer.getPhone());
            existingCustomer.setEmail(customer.getEmail());
            existingCustomer.setAddress(customer.getAddress());
            
            if (customer.getTotalCredit() != null && 
                customer.getTotalCredit().compareTo(existingCustomer.getTotalCredit()) != 0) {
                if (customer.getTotalCredit().compareTo(existingCustomer.getCreditBalance()) < 0) {
                    return ResponseEntity.badRequest()
                        .body("New credit limit cannot be less than current balance");
                }
                existingCustomer.setTotalCredit(customer.getTotalCredit());
            }
            
            Customer updatedCustomer = customerService.updateCustomer(existingCustomer, adminId);
            return ResponseEntity.ok(updatedCustomer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to update customer: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/settle")
    public ResponseEntity<?> settleBalance(@PathVariable Long id,
                                         @RequestBody Transaction transaction) {
        try {
            Long adminId = 1L; // TODO: Get this from security context
            Customer customer = customerService.getCustomerById(id, adminId)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

            if (transaction.getAmount().compareTo(customer.getCreditBalance()) > 0) {
                return ResponseEntity.badRequest()
                    .body("Settlement amount cannot exceed outstanding balance");
            }

            BigDecimal newBalance = customer.getCreditBalance().subtract(transaction.getAmount());
            customer.setCreditBalance(newBalance);
            customerService.updateCustomer(customer, adminId);

            transaction.setCustomer(customer);
            transaction.setType(TransactionType.SETTLEMENT);
            transaction.setStatus("COMPLETED");
            transaction.setTransactionDate(LocalDateTime.now());
            transaction.setAdminId(adminId);
            Transaction savedTransaction = transactionService.saveTransaction(transaction);

            return ResponseEntity.ok(savedTransaction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to process payment: " + e.getMessage());
        }
    }
}
