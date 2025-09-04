CREATE TABLE listings (
  id                  UUID PRIMARY KEY,
  product_id          UUID NOT NULL REFERENCES products (id)  ON DELETE RESTRICT,
  platform_id         UUID NOT NULL REFERENCES platforms (id) ON DELETE RESTRICT,
  external_listing_id TEXT NOT NULL,
  status              TEXT NOT NULL,
  price               NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (price >= 0),
  quantity_listed     INTEGER NOT NULL DEFAULT 0 CHECK (quantity_listed >= 0),

  CONSTRAINT uq_listings_external_per_platform
    UNIQUE (platform_id, external_listing_id)
);

CREATE INDEX idx_listings_product   ON listings (product_id);
CREATE INDEX idx_listings_platform  ON listings (platform_id);
CREATE INDEX idx_listings_status    ON listings (status);