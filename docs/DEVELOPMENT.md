# Development Guide

Complete guide for setting up and working with the Hopper application.

## Prerequisites

### Required Software
- **Java**: Version 21 or 24 (application supports both)
- **PostgreSQL**: For production database (or local development)
- **Gradle**: Wrapper included in repository
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code with Java extensions

### Optional Tools
- **Docker**: For containerized PostgreSQL during development
- **Postman**: For API testing and development
- **Git**: Version control (obviously!)

## Environment Setup

### Development Environment (H2 Database)
The quickest way to get started is using the built-in H2 database:

```bash
# Clone the repository
git clone <repository-url>
cd hopper

# Build the project
./gradlew build

# Run with development profile (uses H2)
./gradlew bootRun
```

The application will start on `http://localhost:8080` with:
- **H2 Database**: File-based database in `data/hopper.mv.db`
- **H2 Console**: Available at `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:file:./data/hopper;MODE=PostgreSQL;NON_KEYWORDS=VALUE`
  - Username: `sa`
  - Password: (empty)

### Production Environment (PostgreSQL)

For production or PostgreSQL development:

```bash
# Set required environment variables
export DATABASE_URL=jdbc:postgresql://localhost:5432/hopper
export DATABASE_USERNAME=hopper_user
export DATABASE_PASSWORD=your_secure_password
export JWT_SECRET=your_jwt_secret_key_minimum_256_bits
export CREDENTIAL_MASTER_KEY=your_32_plus_character_encryption_key

# Run with production profile
./gradlew bootRun --args='--spring.profiles.active=prod'
```

### Docker PostgreSQL Setup

For local PostgreSQL development:

```bash
# Start PostgreSQL container
docker run --name hopper-postgres \
  -e POSTGRES_DB=hopper \
  -e POSTGRES_USER=hopper_user \
  -e POSTGRES_PASSWORD=dev_password \
  -p 5432:5432 \
  -d postgres:15

# Set environment variables for development
export DATABASE_URL=jdbc:postgresql://localhost:5432/hopper
export DATABASE_USERNAME=hopper_user
export DATABASE_PASSWORD=dev_password
export JWT_SECRET=dev_jwt_secret_key_for_development_only_not_production_256_bits
export CREDENTIAL_MASTER_KEY=dev_encryption_key_32_characters_minimum_for_development
```

## Development Commands

### Building and Running
```bash
./gradlew build              # Build the project
./gradlew bootRun           # Run the Spring Boot application
./gradlew clean build       # Clean and rebuild
./gradlew bootBuildImage    # Build OCI container image
./gradlew bootJar           # Create executable JAR
```

### Testing
```bash
./gradlew test              # Run all tests
./gradlew check             # Run tests and all verification tasks
./gradlew test --info       # Run tests with detailed output
./gradlew test --tests="*AuthenticationControllerTest*"  # Run specific tests
```

### Database Operations
```bash
# View current migration status
./gradlew flywayInfo

# Migrate database to latest version
./gradlew flywayMigrate

# Clean database (development only!)
./gradlew flywayClean
```

## Configuration Profiles

### `application-dev.properties` (Development)
- **Database**: H2 file-based with PostgreSQL compatibility
- **Security**: Development JWT and encryption keys (NOT for production)
- **Logging**: SQL statement logging enabled
- **H2 Console**: Enabled for database inspection
- **Debug**: Enhanced logging for development

### `application-prod.properties` (Production)
- **Database**: PostgreSQL with environment variable configuration
- **Security**: Environment-based secrets (required)
- **Monitoring**: Enhanced actuator endpoints
- **Performance**: Optimized settings for production load

### `application-test.properties` (Testing)
- **Database**: In-memory H2 for test isolation
- **Speed**: Optimized for fast test execution
- **Security**: Test-specific configurations

## Development Workflow

### 1. Initial Setup
```bash
# Clone and build
git clone <repository-url>
cd hopper
./gradlew build

# Verify setup
./gradlew bootRun
curl http://localhost:8080/healthz
```

### 2. Database Migrations
```bash
# Create new migration
# File: src/main/resources/db/migration/V{next_version}__{description}.sql

# Example: V015__add_new_feature.sql
# Apply migration
./gradlew flywayMigrate
```

### 3. API Testing
```bash
# Health check
curl http://localhost:8080/healthz

# Login to get JWT token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'

# Use token for authenticated requests
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 4. Testing Strategy
```bash
# Run unit tests
./gradlew test

# Run integration tests
./gradlew integrationTest

# Generate test coverage report
./gradlew jacocoTestReport
# View: build/reports/jacoco/test/html/index.html
```

## Code Style and Conventions

### Java Standards
- **Language**: Java 21/24 with modern features
- **Framework**: Spring Boot 3.5.5 conventions
- **Dependency Injection**: Constructor injection preferred
- **Validation**: Bean validation with custom annotations
- **Error Handling**: Custom exceptions with proper HTTP status codes

### Package Organization
```
src/main/java/dev/fincke/hopper/
├── auth/          # Authentication and JWT management
├── user/          # User management and RBAC
├── catalog/       # Product and listing management
├── order/         # Order processing and buyers
├── platform/      # Platform integration and credentials
└── config/        # Security and application configuration
```

### Comment Conventions
Follow patterns documented in `docs/COMMENT-STYLE.md`:
- **Section markers**: `// *` for major sections
- **Explain "why"**: Not just "what" the code does
- **Spring annotations**: Explain business purpose
- **Database relationships**: Document implications

### DTO Patterns
```java
// Request DTOs: Input validation and data binding
public class CreateUserRequest {
    @NotBlank
    private String username;
    // ...
}

// Response DTOs: Controlled data exposure
public class UserResponse {
    private UUID id;
    private String username;
    // Never expose sensitive fields
}
```

## Security Development

### Authentication Testing
```bash
# Create test user (requires ADMIN role)
curl -X POST http://localhost:8080/api/users \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"TestPass123","roles":["USER"]}'

# Test authentication
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"TestPass123"}'
```

### Credential Encryption Testing
```bash
# Create encrypted credential (requires ADMIN)
curl -X POST http://localhost:8080/api/platform-credentials \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"platformId":"uuid","credentialKey":"api_key","credentialValue":"secret123","active":true}'

# Verify encryption (value should be redacted)
curl -X GET http://localhost:8080/api/platform-credentials \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

## Common Development Tasks

### Adding New Entity
1. Create entity class in appropriate domain package
2. Create repository interface extending JpaRepository
3. Create service interface and implementation
4. Create DTO classes for requests/responses
5. Create controller with REST endpoints
6. Add database migration script
7. Write unit and integration tests

### Adding New API Endpoint
1. Add method to service interface/implementation
2. Add controller endpoint with proper security annotations
3. Create/update DTOs as needed
4. Add validation rules
5. Update API documentation
6. Write tests for new functionality

### Database Schema Changes
1. Create new Flyway migration: `V{version}__{description}.sql`
2. Test migration on development database
3. Update entity classes to match schema
4. Update tests to handle schema changes
5. Document changes in CHANGELOG.md

## Performance Considerations

### Database Optimization
- **Indexing**: Critical for foreign keys and frequent queries
- **Connection Pooling**: Configured for production load
- **Query Optimization**: Use @Query for complex operations
- **Lazy Loading**: Proper JPA fetch strategies

### Security Performance
- **JWT Caching**: Token validation optimizations
- **Encryption**: Credential encryption designed for performance
- **Password Hashing**: BCrypt with appropriate rounds
- **Session Management**: Stateless architecture

## Debugging

### Common Issues
```bash
# Port already in use
./gradlew bootRun --args='--server.port=8081'

# Database connection issues
# Check environment variables and database status

# JWT token issues
# Verify JWT_SECRET is set and tokens haven't expired

# H2 console access
# Ensure development profile is active
```

### Logging Configuration
```bash
# Enable debug logging for specific packages
./gradlew bootRun --args='--logging.level.dev.fincke.hopper=DEBUG'

# Enable SQL logging
./gradlew bootRun --args='--logging.level.org.hibernate.SQL=DEBUG'
```

## IDE Setup

### IntelliJ IDEA
- **Project SDK**: Java 21 or 24
- **Build Tool**: Gradle (auto-import enabled)
- **Code Style**: Java Google Style (optional)
- **Plugins**: Spring Boot, Lombok (if used), Database Navigator

### VS Code
- **Extensions**: Extension Pack for Java, Spring Boot Extension Pack
- **Settings**: Configure Java runtime and Gradle integration
- **Database**: SQLite/PostgreSQL extensions for database access

## Next Development Priorities

### Platform Integrations
- **eBay API Client**: Implement eBay SDK integration
- **Amazon API Client**: Amazon Marketplace Web Service
- **Other Platforms**: TCGPlayer, StockX, Facebook Marketplace

### Infrastructure Enhancements
- **Batch Processing**: Spring Batch jobs for synchronization
- **Caching**: Redis or in-memory caching strategy
- **Rate Limiting**: API throttling and protection
- **Monitoring**: Enhanced metrics and alerting

### API Improvements
- **OpenAPI Documentation**: Swagger integration
- **Pagination**: Implement consistent pagination
- **Filtering**: Advanced search and filtering capabilities
- **Versioning**: API versioning strategy