package com.restaurant.creditmanagement.repository;

import com.restaurant.creditmanagement.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByAdminId(Long adminId);
    List<MenuItem> findByCategoryOrderByName(String category);
}
