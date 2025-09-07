# Runbook

Basics
- Start: `./gradlew bootRun`
- Health: `GET /healthz` → `{status: ok, ...}`
- Logs: stdout (app), Gradle logs for build issues

Configuration
- Profiles: `dev`, `test`, `prod`
- Properties: see `src/main/resources/application-*.properties`

Database
- Dev/Test: H2 for tests, Postgres for dev/prod
- Migrations: Flyway auto-applies on startup

Common issues
- Java version mismatch: use Java 21
- DB connection errors: verify URL/creds in `application-dev.properties`

Operational next steps
- Containerize app and define health/readiness probes
- Add metrics via Actuator and dashboards
