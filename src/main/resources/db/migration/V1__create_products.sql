CREATE TABLE products (
  id          UUID PRIMARY KEY,
  name        TEXT    NOT NULL,
  sku         TEXT,
  description TEXT,
  price       NUMERIC(12,2) NOT NULL DEFAULT 0,
  quantity    INTEGER NOT NULL DEFAULT 0,

  CONSTRAINT uq_products_name UNIQUE (name),
  CONSTRAINT uq_products_sku  UNIQUE (sku)
);
