-- Drop existing sequences if they exist
DROP SEQUENCE IF EXISTS orders_id_seq;
DROP SEQUENCE IF EXISTS order_items_id_seq;

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

-- Reset the sequences to the current maximum values
SELECT setval('orders_id_seq', COALESCE((SELECT MAX(id) FROM orders), 1), false);
SELECT setval('order_items_id_seq', COALESCE((SELECT MAX(id) FROM order_items), 1), false);