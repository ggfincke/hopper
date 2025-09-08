CREATE TABLE orders (
  id                 UUID PRIMARY KEY,
  platform_id        UUID NOT NULL REFERENCES platforms (id) ON DELETE RESTRICT,
  buyer_id           UUID REFERENCES buyers (id) ON DELETE SET NULL,
  external_order_id  TEXT NOT NULL,
  status             TEXT NOT NULL,
  total_amount       NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (total_amount >= 0),
  order_date         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT uq_orders_external_per_platform
    UNIQUE (platform_id, external_order_id)
);

CREATE INDEX idx_orders_platform   ON orders (platform_id);
CREATE INDEX idx_orders_buyer      ON orders (buyer_id);
CREATE INDEX idx_orders_order_date ON orders (order_date DESC);