CREATE TABLE platforms (
  id            UUID PRIMARY KEY,
  name          TEXT NOT NULL,
  platform_type TEXT NOT NULL,
  CONSTRAINT uq_platforms_name UNIQUE (name)
);

CREATE INDEX idx_platforms_type ON platforms (platform_type);