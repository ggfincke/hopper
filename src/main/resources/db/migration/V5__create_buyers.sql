CREATE TABLE buyers (
  id    UUID PRIMARY KEY,
  email TEXT,
  name  TEXT
);

CREATE UNIQUE INDEX uq_buyers_email ON buyers (email);