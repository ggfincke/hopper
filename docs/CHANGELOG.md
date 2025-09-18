# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## 2025-09-18

### Added
- **Authentication & Authorization System**: Complete JWT-based security implementation
  - JWT token generation, validation, and refresh mechanism with Spring Security integration
  - Role-based access control (RBAC) with three system roles: ADMIN, USER, API_CLIENT
  - Account management with locking, failed login tracking, and password change functionality
  - Secure HTTP-only cookies for refresh tokens with configurable expiration
  - Authentication filters and user details service for comprehensive security
  - Method-level security with @PreAuthorize annotations
  - CORS configuration for cross-origin request support
  - Security endpoints: login, logout, refresh, validate, and user profile

- **Credential Encryption**: Complete security implementation for platform credentials
  - Domain primitives: EncryptedCredential, encryption exceptions, service interfaces
  - AES-GCM-256 encryption service with key derivation and integrity validation
  - JPA entity listener for transparent encryption/decryption lifecycle
  - Database schema migration with encryption metadata columns
  - Comprehensive test coverage for encryption workflows
  - Configuration properties for encryption parameters and key management
  - Service methods for credential decryption, validation, and re-encryption

## 2025-09-16

### Changed
- **Data Model Improvements**: Enhanced null safety and validation across entities
  - Product: Made SKU nullable with trimming, Buyer: unique email constraint
  - OrderAddress: Added country field, removed redundant index
  - PlatformCredential: Changed to primitive boolean with convenience methods
- **Schema Updates**: Added NOT NULL constraints and foreign key relationships

### Fixed
- Removed duplicate index on order_addresses.order_id

## 2025-09-10

### Added
- **User Management System**: Complete authentication and authorization implementation with User and Role entities, BCrypt password encoding, account locking, and comprehensive service layer
- **Database Schema for RBAC**: User authentication tables (users, roles, user_roles) with proper indexes, constraints, and predefined system roles (ADMIN, USER, API_CLIENT)
- **User REST API**: CRUD endpoints for user operations including password change, role assignment, account enable/disable, and search functionality
- **JWT Dependencies**: Added JJWT library dependencies for token-based authentication including API, implementation, and Jackson binding modules
- **JWT Configuration Properties**: Added JWT configuration settings across all application profiles including token expiration, issuer, audience, and security settings
- **JWT Authentication Implementation**: Complete JWT authentication system with Spring Security integration including token generation, validation, authentication filters, and user details service

## 2025-09-09 

### Added
- **Complete Service Layer Architecture**: Three-layer architecture (Controller → Service → Repository) for all domain entities (Product, Listing, Buyer, OrderAddress, Order, OrderItem, Platform, PlatformCredential, PlatformFee)
- **Service Layer Business Logic**: Interface-based design with CRUD operations, validation, and domain-specific operations (stock management, status transitions, geographic queries, credential key management, fee calculations and aggregations)
- **Comprehensive DTO Pattern**: Request/response objects with validation and normalization for all entities including PlatformFee DTOs with business rule validation
- **Domain Exception Handling**: Custom business exceptions (NotFound, Duplicate, Validation, InsufficientStock, InvalidStatus, DuplicateCredentialKey, DuplicateFeeType, InvalidFeeAmount, PlatformFeeNotFound) for all service operations
- **Enhanced Repository Layer**: Business-specific query methods with Optional returns for all entities

### Changed
- **All Controllers**: Refactored from direct repository access to service layer pattern
- **Transaction Management**: Proper @Transactional annotations with read-only defaults
- **Code Consistency**: Improved formatting and comment capitalization across all layers

## 2025-09-08

### Changed
- **Package Reorganization**: Restructured all entities into domain-driven package hierarchy
  - Moved catalog-related entities (Product, Listing) to `catalog/` package with nested subpackages
  - Moved order-related entities (Order, OrderItem, Buyer, OrderAddress) to `order/` package with nested subpackages
  - Moved platform-related entities (Platform, PlatformCredential, PlatformFee) to `platform/` package with nested subpackages
  - Improved code organization and modularity while maintaining all functionality and API contracts

### Previously Added
- **PlatformCredentials**: JPA entity, repository, and REST controller; many-to-one relationship to Platform; encrypted credential storage with key-value pairs; active/inactive status tracking; unique constraint on (platform_id, credential_key)
- **PlatformFees**: JPA entity, repository, and REST controller; many-to-one relationship to Order; fee tracking by type with monetary amounts; aggregation queries for totals by order and fee type

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
