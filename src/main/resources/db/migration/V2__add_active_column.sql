ALTER TABLE customers ADD COLUMN active boolean DEFAULT true;
UPDATE customers SET active = true WHERE active IS NULL;
ALTER TABLE customers ALTER COLUMN active SET NOT NULL;