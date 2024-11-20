package com.restaurant.creditmanagement.controller;

import com.restaurant.creditmanagement.model.Customer;
import com.restaurant.creditmanagement.model.Transaction;
import com.restaurant.creditmanagement.repository.CustomerRepository;
import com.restaurant.creditmanagement.repository.TransactionRepository;
import com.restaurant.creditmanagement.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping
    public String listCustomers(Model model, HttpSession session) {
        List<Customer> customers = customerService.getAllCustomers();
        model.addAttribute("customers", customers);
        return "customers/list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "customers/form";
    }

    @PostMapping("/new")
    public String addCustomer(@ModelAttribute Customer customer, 
                            RedirectAttributes redirectAttributes) {
        try {
            // Ensure credit balance starts at 0
            customer.setCreditBalance(BigDecimal.ZERO);
            
            // Ensure total credit is not null
            if (customer.getTotalCredit() == null) {
                customer.setTotalCredit(BigDecimal.ZERO);
            }
            
            customerService.createCustomer(customer);
            redirectAttributes.addFlashAttribute("success", "Customer added successfully!");
            return "redirect:/customers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add customer: " + e.getMessage());
            return "redirect:/customers/new";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Customer> customerOpt = customerService.getCustomerById(id);
        if (customerOpt.isPresent()) {
            model.addAttribute("customer", customerOpt.get());
            return "customers/form";
        }
        throw new RuntimeException("Customer not found");
    }

    @PostMapping("/edit/{id}")
    public String updateCustomer(@PathVariable Long id, 
                               @ModelAttribute Customer customer,
                               RedirectAttributes redirectAttributes) {
        try {
            Optional<Customer> existingCustomerOpt = customerService.getCustomerById(id);
            if (!existingCustomerOpt.isPresent()) {
                throw new RuntimeException("Customer not found");
            }
            
            Customer existingCustomer = existingCustomerOpt.get();
            
            // Update the existing customer's fields
            existingCustomer.setName(customer.getName());
            existingCustomer.setPhone(customer.getPhone());
            existingCustomer.setEmail(customer.getEmail());
            existingCustomer.setAddress(customer.getAddress());
            
            // Handle credit limit changes
            if (customer.getTotalCredit() != null && 
                customer.getTotalCredit().compareTo(existingCustomer.getTotalCredit()) != 0) {
                // Validate that new credit limit is not less than current balance
                if (customer.getTotalCredit().compareTo(existingCustomer.getCreditBalance()) < 0) {
                    redirectAttributes.addFlashAttribute("error", 
                        "New credit limit cannot be less than current balance (₹" + 
                        existingCustomer.getCreditBalance() + ")");
                    return "redirect:/customers/edit/" + id;
                }
                existingCustomer.setTotalCredit(customer.getTotalCredit());
            }
            
            customerService.updateCustomer(existingCustomer);
            redirectAttributes.addFlashAttribute("success", "Customer updated successfully!");
            return "redirect:/customers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update customer: " + e.getMessage());
            return "redirect:/customers/edit/" + id;
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteCustomer(id);
            redirectAttributes.addFlashAttribute("success", "Customer deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting customer: " + e.getMessage());
        }
        return "redirect:/customers";
    }

    @PostMapping("/settle-balance/{id}")
    public String settleBalance(@PathVariable Long id, 
                              @RequestParam(required = false) BigDecimal settlementAmount,
                              RedirectAttributes redirectAttributes) {
        try {
            if (settlementAmount != null) {
                customerService.settleBalance(id, settlementAmount);
            } else {
                customerService.settleBalance(id);
            }
            redirectAttributes.addFlashAttribute("success", "Balance settled successfully!");
            return "redirect:/customers/view/" + id;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/customers/view/" + id;
        }
    }

    @GetMapping("/view/{id}")
    public String viewCustomer(@PathVariable Long id, Model model) {
        Optional<Customer> customerOpt = customerService.getCustomerById(id);
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Customer not found");
        }

        Customer customer = customerOpt.get();
        List<Transaction> transactions = transactionRepository.findByCustomerIdOrderByTransactionDateDesc(id);

        model.addAttribute("customer", customer);
        model.addAttribute("transactions", transactions);
        return "customers/view";
    }
}
