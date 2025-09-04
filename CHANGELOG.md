# Changelog

## [Unreleased] - 2025-09-03

### Added
- **Database Schema**: Complete Flyway migrations for core entities (products, platforms, credentials, listings, orders, buyers, addresses, fees)
- **Platform Management**: REST API with CRUD endpoints and repository layer
- **Health Check**: Application version info in health endpoint
- **Multi-Environment DB**: PostgreSQL for production, H2 for testing

### Changed
- **Kotlin**: Upgraded to 2.2.0
- **Flyway**: Enabled with validate DDL mode

### Infrastructure
- Spring Boot 3.5.5 with JPA, Security, Batch, Actuator
- Gradle build system with container support

## 2025-09-02

### Added
- **Project Bootstrap**: Initial Spring Boot 3.5.5 project setup with Kotlin 1.9.25
- **Build System**: Gradle wrapper and build configuration
- **Basic Application**: Main application class and starter configuration
- **Testing Framework**: Basic test structure with Spring Boot Test
- **Git Configuration**: Repository initialization with .gitignore and .gitattributes