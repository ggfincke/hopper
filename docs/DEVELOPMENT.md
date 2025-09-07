# Development

Prereqs
- Java 21
- PostgreSQL (prod), H2 (tests)
- Gradle wrapper

Common tasks
- Build: `./gradlew build`
- Run: `./gradlew bootRun`
- Test: `./gradlew test`

Environments
- `application-dev.properties`: local dev defaults
- `application-test.properties`: test profile (H2)
- `application-prod.properties`: production (PostgreSQL)

Running locally
- Export DB vars or edit `application-dev.properties` as needed.
- Start app: `./gradlew bootRun` then visit `http://localhost:8080/healthz`.

Coding style
- Java, Spring Boot conventions; use DTOs for API responses.
- Prefer constructor injection.

Next steps
- Add service layer and validation.
- Introduce pagination for list endpoints.
