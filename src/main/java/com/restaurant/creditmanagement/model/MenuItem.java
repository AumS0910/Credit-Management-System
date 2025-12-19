package com.restaurant.creditmanagement.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;

@Document(collection = "menu_items")
public class MenuItem {

    @Id
    private String id;
    private String adminId; // Changed from Long to String
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String imageUrl;
    private Integer prepTime;
    private Boolean isSpecial;
    private Boolean available;
    private Integer rating;

    // Add getters and setters for all fields
    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public Boolean getIsSpecial() { return isSpecial; }
    public void setIsSpecial(Boolean isSpecial) { this.isSpecial = isSpecial; }

    public Boolean isAvailable() { // Changed from getIsAvailable to isAvailable
        return available;
    }

    public void setAvailable(Boolean available) { // Changed from setIsAvailable to setAvailable
        this.available = available;
    }

    public Integer getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(Integer prepTime) {
        this.prepTime = prepTime;
    }
}
