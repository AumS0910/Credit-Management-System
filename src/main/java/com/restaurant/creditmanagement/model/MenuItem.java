package com.restaurant.creditmanagement.model;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "menu_items")
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(length = 500)
    private String description;

    @Column(name = "category")
    private String category;

    @Column(name = "is_available")
    private boolean available = true;
    
    @Column(name = "admin_id", nullable = false)
    private Long adminId;
}
