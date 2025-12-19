package com.restaurant.creditmanagement.repository;

import com.restaurant.creditmanagement.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {
    List<Customer> findByAdminId(String adminId);

    @Query("{ 'name': { $regex: ?0, $options: 'i' }, 'adminId': ?1 }")
    List<Customer> searchByName(String query, String adminId);

    List<Customer> findTop5ByAdminIdOrderByCreditBalanceDesc(String adminId);

    @Query(value = "{ 'adminId': ?0 }", fields = "{ 'creditBalance': 1 }")
    List<Customer> findCreditBalancesByAdminId(String adminId);

    long countByAdminId(String adminId);

    Long countByAdminId(String adminId);
}
