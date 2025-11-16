-- Basic bootstrap for local development
DO $$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'hopper_user') THEN
      CREATE ROLE hopper_user LOGIN PASSWORD 'dev_password';
   END IF;
END
$$;

-- Create database (cannot use DO block - CREATE DATABASE requires commit)
SELECT 'CREATE DATABASE hopper OWNER hopper_user ENCODING ''UTF8'''
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'hopper')\gexec

\connect hopper

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

ALTER SCHEMA public OWNER TO hopper_user;
GRANT ALL PRIVILEGES ON SCHEMA public TO hopper_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO hopper_user;
