# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## 2025-09-07

### Added
- **Orders**: JPA entity, repository, and REST controller; relationships to Platform; unique (platform_id, external_order_id); status and date indexing
- **OrderItems**: JPA entity, repository, and REST controller; relationships to Order and Listing; quantity and price tracking
- **Buyers**: JPA entity, repository, and REST controller; email validation and name search; optional relationship to Orders
- **OrderAddresses**: JPA entity, repository, and REST controller; one-to-one relationship with Order; address validation and geographic indexing
- **Documentation**: COMMENT-STYLE.md guide with Java beginner-focused comment conventions; section markers, annotation explanations, and relationship documentation patterns

## 2025-09-06

### Added
- **Product API**: REST controller with DTOs; aligned schema and repository
- **Listings**: JPA entity, repository, and REST endpoint; price normalized to decimal(12,2); FKs to Products and Platforms; unique (platform_id, external_listing_id)

### Changed
- **Platforms**: entity cleanups (naming, annotations, indexes) for clarity and consistency
- **Code Style**: Allman brace style enforced across the codebase and applied to new Listings code
- **Documentation**: consolidated and moved root docs to `docs/`; added SCHEMA.md and additional docs placeholders

## 2025-09-05

### Changed
- **Language Migration**: Complete migration from Kotlin to Java
  - Migrated all source code from Kotlin (.kt) to Java (.java)
  - Updated build configuration from build.gradle.kts to build.gradle
  - Converted Gradle settings from Kotlin DSL to Groovy
  - Removed Kotlin-specific dependencies and plugins
  - Maintained all existing functionality and API contracts

## 2025-09-03

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
