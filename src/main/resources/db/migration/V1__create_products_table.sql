-- V1__create_products_table.sql

CREATE TABLE products (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255)   NOT NULL,
    category    VARCHAR(100)   NOT NULL,
    price       NUMERIC(19, 4) NOT NULL,
    created_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

-- Índices compuestos para keyset pagination
-- Siempre incluir id al final como tiebreaker
CREATE INDEX idx_products_price_id    ON products (price ASC,      id ASC);
CREATE INDEX idx_products_name_id     ON products (name ASC,       id ASC);
CREATE INDEX idx_products_category_id ON products (category ASC,   id ASC);
CREATE INDEX idx_products_created_id  ON products (created_at ASC, id ASC);

-- Seed data
INSERT INTO products (name, category, price)
SELECT
    'Product ' || gs,
    (ARRAY['electronics', 'clothing', 'food', 'books'])[floor(random() * 4 + 1)],
    (random() * 1000)::NUMERIC(19, 4)
FROM generate_series(1, 500) gs;
