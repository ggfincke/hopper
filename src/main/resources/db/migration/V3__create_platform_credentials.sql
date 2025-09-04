CREATE TABLE platform_credentials (
  id               UUID PRIMARY KEY,
  platform_id      UUID NOT NULL REFERENCES platforms (id) ON DELETE CASCADE,
  credential_key   TEXT NOT NULL,
  credential_value TEXT NOT NULL,
  is_active        BOOLEAN NOT NULL DEFAULT TRUE,

  CONSTRAINT uq_platform_credentials_key UNIQUE (platform_id, credential_key)
);

CREATE INDEX idx_platform_credentials_platform ON platform_credentials (platform_id);
CREATE INDEX idx_platform_credentials_active   ON platform_credentials (is_active);