-- Create roles table for user authorization
CREATE TABLE roles (
  id          UUID PRIMARY KEY,
  name        VARCHAR(20) NOT NULL UNIQUE,
  description VARCHAR(255)
);

-- Create index for fast role lookups
CREATE INDEX idx_roles_name ON roles (name);

-- Create unique constraint for role name
CREATE UNIQUE INDEX uq_roles_name ON roles (name);

INSERT INTO roles (id, name, description) VALUES 
  (random_uuid(), 'ADMIN', 'System administrator with full access'),
  (random_uuid(), 'USER', 'Regular user with standard permissions'),
  (random_uuid(), 'API_CLIENT', 'API client for programmatic access');
