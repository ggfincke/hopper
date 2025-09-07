# Database Migrations

Tooling
- Flyway, SQL-based migrations under `src/main/resources/db/migration`.

Conventions
- Files named `V{n}__{description}.sql` (e.g., `V1__create_products.sql`).
- One logical change per migration; never edit applied migrations.

Local workflow
1) Create new `V{next}__*.sql` with forward-only DDL.
2) Build/run; Flyway validates and applies outstanding migrations.
3) If a migration fails locally, create a new corrective migration rather than editing history.

Environments
- Validation is enabled; prod should run with `flyway.validateOnMigrate=true`.

Tips
- Index foreign keys and unique constraints.
- Use `decimal(12,2)` for money fields to match code.
