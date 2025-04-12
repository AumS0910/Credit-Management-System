-- Drop constraints first
ALTER TABLE IF EXISTS order_items DROP CONSTRAINT IF EXISTS order_items_order_id_fkey;
ALTER TABLE IF EXISTS order_items DROP CONSTRAINT IF EXISTS order_items_menu_item_id_fkey;
ALTER TABLE IF EXISTS orders DROP CONSTRAINT IF EXISTS orders_customer_id_fkey;

-- Drop tables
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;

-- Drop sequences
DROP SEQUENCE IF EXISTS orders_id_seq CASCADE;
DROP SEQUENCE IF EXISTS order_items_id_seq CASCADE;

-- Recreate orders table
CREATE TABLE orders (
    id BIGINT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    total_amount DECIMAL(19, 2) NOT NULL DEFAULT 0,
    tax DECIMAL(19, 2) NOT NULL DEFAULT 0,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    CONSTRAINT orders_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

-- Recreate order_items table
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    menu_item_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL(19, 2) NOT NULL,
    subtotal DECIMAL(19, 2) NOT NULL,
    CONSTRAINT order_items_order_id_fkey FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT order_items_menu_item_id_fkey FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)
);

-- Create new sequences
CREATE SEQUENCE orders_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE order_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Set the sequences as owned by their respective tables
ALTER SEQUENCE orders_id_seq OWNED BY orders.id;
ALTER SEQUENCE order_items_id_seq OWNED BY order_items.id;

-- Set the default values for the ID columns
ALTER TABLE orders ALTER COLUMN id SET DEFAULT nextval('orders_id_seq');
ALTER TABLE order_items ALTER COLUMN id SET DEFAULT nextval('order_items_id_seq');

-- Reset the sequences to start from 1
ALTER SEQUENCE orders_id_seq RESTART WITH 1;
ALTER SEQUENCE order_items_id_seq RESTART WITH 1;
