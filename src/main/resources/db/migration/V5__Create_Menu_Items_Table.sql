CREATE TABLE menu_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    description VARCHAR(500),
    category VARCHAR(100),
    is_available BOOLEAN DEFAULT TRUE,
    admin_id BIGINT NOT NULL,
    FOREIGN KEY (admin_id) REFERENCES admins(id)
);

-- Insert sample menu items with admin_id (assuming admin with ID 1 exists)
INSERT INTO menu_items (name, price, description, category, admin_id) VALUES
-- Starters
('Vegetable Spring Rolls', 150.00, 'Crispy rolls filled with mixed vegetables', 'Starters', 1),
('Paneer Tikka', 220.00, 'Grilled cottage cheese with Indian spices', 'Starters', 1),
('Chicken Wings', 280.00, 'Spicy chicken wings with dipping sauce', 'Starters', 1),

-- Main Course
('Butter Chicken', 320.00, 'Creamy tomato-based curry with chicken', 'Main Course', 1),
('Palak Paneer', 260.00, 'Cottage cheese in spinach gravy', 'Main Course', 1),
('Biryani', 280.00, 'Fragrant rice dish with mixed vegetables or chicken', 'Main Course', 1),
('Dal Makhani', 220.00, 'Creamy black lentils', 'Main Course', 1),

-- Breads
('Butter Naan', 40.00, 'Soft bread from tandoor', 'Breads', 1),
('Roti', 30.00, 'Whole wheat bread', 'Breads', 1),
('Garlic Naan', 50.00, 'Naan with garlic and butter', 'Breads', 1),

-- Desserts
('Gulab Jamun', 120.00, 'Sweet milk dumplings in sugar syrup', 'Desserts', 1),
('Ice Cream', 100.00, 'Choice of vanilla, chocolate, or strawberry', 'Desserts', 1),
('Kheer', 140.00, 'Traditional rice pudding', 'Desserts', 1),

-- Beverages
('Masala Chai', 40.00, 'Indian spiced tea', 'Beverages', 1),
('Cold Drink', 60.00, 'Assorted soft drinks', 'Beverages', 1),
('Lassi', 80.00, 'Sweet or salty yogurt drink', 'Beverages', 1);
