package com.restaurant.creditmanagement.repository;

import com.restaurant.creditmanagement.model.MenuItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MenuItemRepository extends MongoRepository<MenuItem, String> {
    List<MenuItem> findByAdminId(String adminId);
    List<MenuItem> findByCategoryOrderByName(String category);
}
