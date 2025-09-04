CREATE TABLE platform_fees (
  id       UUID PRIMARY KEY,
  order_id UUID NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
  fee_type TEXT NOT NULL,
  amount   NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (amount >= 0)
);

CREATE INDEX idx_platform_fees_order   ON platform_fees (order_id);
CREATE INDEX idx_platform_fees_type    ON platform_fees (fee_type);