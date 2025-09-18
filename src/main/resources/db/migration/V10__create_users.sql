-- Create users table with comprehensive user management fields
CREATE TABLE users (
  id                     UUID PRIMARY KEY,
  username               VARCHAR(50) NOT NULL UNIQUE,
  email                  TEXT NOT NULL UNIQUE,
  password               VARCHAR(60) NOT NULL,
  enabled                BOOLEAN NOT NULL DEFAULT TRUE,
  account_locked         BOOLEAN NOT NULL DEFAULT FALSE,
  failed_login_attempts  INTEGER NOT NULL DEFAULT 0,
  created_at             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for fast user lookups during authentication and search
CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_enabled ON users (enabled);
CREATE INDEX idx_users_account_locked ON users (account_locked);
CREATE INDEX idx_users_failed_login_attempts ON users (failed_login_attempts);

-- Create unique constraints for business logic enforcement
CREATE UNIQUE INDEX uq_users_username ON users (username);
CREATE UNIQUE INDEX uq_users_email ON users (email);