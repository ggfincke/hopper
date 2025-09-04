CREATE TABLE order_items (
  id         UUID PRIMARY KEY,
  order_id   UUID NOT NULL REFERENCES orders (id)   ON DELETE CASCADE,
  listing_id UUID NOT NULL REFERENCES listings (id) ON DELETE RESTRICT,
  quantity   INTEGER NOT NULL CHECK (quantity > 0),
  price      NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (price >= 0)
);

CREATE INDEX idx_order_items_order   ON order_items (order_id);
CREATE INDEX idx_order_items_listing ON order_items (listing_id);