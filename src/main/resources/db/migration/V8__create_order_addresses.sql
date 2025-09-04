CREATE TABLE order_addresses (
  id          UUID PRIMARY KEY,
  order_id    UUID NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
  street      TEXT NOT NULL,
  city        TEXT NOT NULL,
  state       TEXT,
  postal_code TEXT,
  country     TEXT NOT NULL,

  CONSTRAINT uq_order_addresses_order UNIQUE (order_id)
);

CREATE INDEX idx_order_addresses_order ON order_addresses (order_id);