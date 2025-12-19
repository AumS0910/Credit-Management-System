-- Insert default admin if not exists
INSERT INTO admins (username, password, name, email, active, created_at)
SELECT 'admin', 'admin123', 'System Administrator', 'admin@restaurant.com', TRUE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM admins WHERE username = 'admin'
);
