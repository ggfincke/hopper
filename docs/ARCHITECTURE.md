# Architecture Overview

High-level view of Hopper’s backend.

- Layers: Web (controllers) → Service (business rules, WIP) → Data (Spring Data JPA repositories) → DB (PostgreSQL/Flyway)
- Tech: Spring Boot 3.5, Java 21, JPA/Hibernate, Flyway, Actuator.
- Modules (package-level):
  - `api`: health and cross-cutting endpoints.
  - `products`, `platforms`, `listings`: domain controllers + repositories + entities.
- Configuration: `application-*.properties` select DB and env settings.
- Migrations: `src/main/resources/db/migration` using Flyway `V__*.sql` files.

Next
- Introduce service layer for validations and orchestration.
- Add DTO mappers and request/response validation.
- Define error handling via `@ControllerAdvice`.
