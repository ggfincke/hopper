-- Persist encryption metadata so we can decrypt credentials and manage rotations safely
-- Store algorithm version, salt, key id, and audit timestamp next to the ciphertext

-- H2 requires separate ALTER statements, so we avoid vendor-specific multi-column syntax
ALTER TABLE platform_credentials ADD COLUMN encryption_version VARCHAR(50);
ALTER TABLE platform_credentials ADD COLUMN salt TEXT;
ALTER TABLE platform_credentials ADD COLUMN encrypted_at TIMESTAMP;
ALTER TABLE platform_credentials ADD COLUMN key_id VARCHAR(100);

-- Index metadata columns used by rotation and auditing workflows
CREATE INDEX idx_platform_credentials_encryption_version ON platform_credentials (encryption_version);
CREATE INDEX idx_platform_credentials_encrypted_at ON platform_credentials (encrypted_at);
CREATE INDEX idx_platform_credentials_key_id ON platform_credentials (key_id);

-- Document columns so DBAs understand how encryption metadata is used
COMMENT ON COLUMN platform_credentials.encryption_version IS 'Version of encryption algorithm used (e.g., AES-GCM-256-V1)';
COMMENT ON COLUMN platform_credentials.salt IS 'Base64-encoded salt used for key derivation';
COMMENT ON COLUMN platform_credentials.encrypted_at IS 'Timestamp when credential was last encrypted';
COMMENT ON COLUMN platform_credentials.key_id IS 'Unique identifier for tracking key rotation';
