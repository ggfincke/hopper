CREATE TABLE products (
  id                  UUID PRIMARY KEY,
  sku                 TEXT    NOT NULL,
  title               TEXT    NOT NULL,
  quantity_on_hand    INTEGER NOT NULL DEFAULT 0 CHECK (quantity_on_hand >= 0),
  quantity_available  INTEGER NOT NULL DEFAULT 0 CHECK (quantity_available >= 0),
  cost                NUMERIC(12,2) NOT NULL DEFAULT 0,

  CONSTRAINT uq_products_sku UNIQUE (sku)
);

CREATE INDEX idx_products_title ON products (title);