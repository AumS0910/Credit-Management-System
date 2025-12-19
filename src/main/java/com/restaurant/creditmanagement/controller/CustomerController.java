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
            List<Customer> customers = customerService.getAllCustomers(adminIdStr);
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCustomer(@RequestBody Customer customer,
                                          @RequestHeader("Admin-ID") String adminIdStr) {
        try {
            customer.setCreditBalance(BigDecimal.ZERO); // Set initial balance to 0
            customer.setActive(true);
            customer.setAdminId(adminIdStr);
            Customer savedCustomer = customerService.createCustomer(customer, adminIdStr);
            return ResponseEntity.ok(savedCustomer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to add customer: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteCustomer(@PathVariable String id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting customer: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Customer>> searchCustomers(@RequestParam String query,
                                                         @RequestHeader("Admin-ID") String adminIdStr) {
        try {
            List<Customer> customers = customerService.searchCustomers(query, adminIdStr);
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    

    

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomer(@PathVariable String id,
                                        @RequestHeader("Admin-ID") String adminIdStr) {
        try {
            Optional<Customer> customer = customerService.getCustomerById(id, adminIdStr);
            return customer.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching customer: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<?> updateCustomer(@PathVariable String id,
                                           @RequestBody Customer customer,
                                           @RequestHeader("Admin-ID") String adminIdStr) {
        try {
            Optional<Customer> existingCustomerOpt = customerService.getCustomerById(id, adminIdStr);

            if (!existingCustomerOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Customer existingCustomer = existingCustomerOpt.get();
            existingCustomer.setName(customer.getName());
            existingCustomer.setPhone(customer.getPhone());
            existingCustomer.setEmail(customer.getEmail());
            existingCustomer.setAddress(customer.getAddress());

            if (customer.getTotalCredit() != null) {
                if (customer.getTotalCredit().compareTo(existingCustomer.getCreditBalance()) < 0) {
                    return ResponseEntity.badRequest()
                            .body("New credit limit cannot be less than current balance");
                }
                existingCustomer.setTotalCredit(customer.getTotalCredit());
            }

            Customer updatedCustomer = customerService.updateCustomer(existingCustomer, adminIdStr);
            return ResponseEntity.ok(updatedCustomer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update customer: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/settle")
        public ResponseEntity<?> settleBalance(
                @PathVariable String id,
                @RequestBody Transaction transaction,
                @RequestHeader("Admin-ID") String adminIdStr) {
            try {
                Optional<Customer> customerOpt = customerService.getCustomerById(id, adminIdStr);

                if (!customerOpt.isPresent()) {
                    return ResponseEntity.notFound().build();
                }

                Customer customer = customerOpt.get();
                BigDecimal settlementAmount = transaction.getAmount();

                // Validate settlement amount
                if (settlementAmount.compareTo(BigDecimal.ZERO) <= 0 ||
                    settlementAmount.compareTo(customer.getCreditBalance()) > 0) {
                    return ResponseEntity.badRequest()
                        .body("Invalid settlement amount");
                }

                // Set transaction details
                transaction.setCustomerId(id);
                transaction.setType(TransactionType.SETTLEMENT.toString());
                transaction.setStatus("COMPLETED");
                transaction.setTransactionDate(LocalDateTime.now());
                transaction.setAdminId(adminIdStr);

                // Update customer balance
                BigDecimal newBalance = customer.getCreditBalance().subtract(settlementAmount);
                customer.setCreditBalance(newBalance);

                // Save transaction and update customer
                transactionRepository.save(transaction);
                Customer updatedCustomer = customerService.updateCustomer(customer, adminIdStr);

                return ResponseEntity.ok(updatedCustomer);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to settle balance: " + e.getMessage());
            }
        }
}