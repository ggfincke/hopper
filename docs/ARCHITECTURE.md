# Architecture Overview

Comprehensive view of Hopper's production-ready backend infrastructure.

## System Architecture

**Layers**: Web (Controllers) → Security (JWT/Auth) → Service (Business Logic) → Data (JPA Repositories) → Database (PostgreSQL/H2)

**Technology Stack**:
- **Framework**: Spring Boot 3.5.5 with Java (21/24)
- **Security**: Spring Security with JWT authentication
- **Data**: JPA/Hibernate with Spring Data repositories
- **Database**: PostgreSQL (production) / H2 (development)
- **Migrations**: Flyway with versioned schema management
- **Monitoring**: Spring Boot Actuator
- **Build**: Gradle with wrapper
- **Encryption**: AES-GCM-256 with PBKDF2 key derivation

## Domain Modules (Package Architecture)

### **Authentication & Security** (`auth/`, `user/`, `config/`)
- **JWT Token Management**: Access/refresh token generation and validation
- **User Management**: Registration, authentication, account management
- **Role-Based Access Control**: ADMIN, USER, API_CLIENT roles
- **Security Configuration**: CORS, endpoint protection, method-level security
- **Password Security**: BCrypt encoding, failed login tracking, account locking

### **Platform Integration** (`platform/`)
- **Platform Management**: Marketplace configuration and metadata
- **Credential Encryption**: AES-GCM-256 encrypted API credential storage
- **Fee Management**: Platform commission and fee tracking
- **Key Rotation**: Automated re-encryption and key management
- **Audit Logging**: Complete encryption operation tracking

### **Product Catalog** (`catalog/`)
- **Product Management**: SKU, name, description, pricing, and quantity
- **Listing Management**: Product-to-platform mapping with external IDs
- **Inventory Tracking**: Stock levels per platform
- **Price Management**: Platform-specific pricing strategies

### **Order Processing** (`order/`)
- **Order Management**: Complete order lifecycle with platform integration
- **Buyer Management**: Customer information and contact details
- **Order Items**: Product quantity and pricing per order
- **Address Management**: Shipping and billing address handling
- **External Integration**: Platform-specific order ID mapping

### **Cross-Cutting Concerns** (`api/`)
- **Health Checks**: Application status and dependency monitoring
- **Error Handling**: Comprehensive exception management
- **Validation**: Bean validation with custom business rules
- **Configuration**: Profile-based environment configuration

## Data Architecture

### **Database Design**
```
Users ←→ UserRoles ←→ Roles (RBAC)
Products → Listings → Platforms
Orders → OrderItems → Listings
Orders → Buyers, OrderAddresses
Platforms → PlatformCredentials (encrypted)
Platforms → PlatformFees
```

### **Key Relationships**
- **Many-to-Many**: Users ↔ Roles for flexible permission assignment
- **One-to-Many**: Platforms → Listings, Orders, Credentials, Fees
- **Foreign Keys**: Proper referential integrity with cascading
- **Unique Constraints**: Business logic enforcement (e.g., platform + external_id)
- **Indexing**: Performance optimization for queries and joins

### **Encryption Architecture**
- **Master Key**: Environment-derived key for credential encryption
- **Salt Generation**: Unique salt per credential (32 bytes)
- **Key Derivation**: PBKDF2WithHmacSHA256 (100k iterations)
- **IV Generation**: Random 96-bit IV per operation
- **Authentication**: GCM mode with 128-bit authentication tag

## Security Architecture

### **Authentication Flow**
1. **Login**: Username/password → JWT access token + HTTP-only refresh cookie
2. **Authorization**: Bearer token validation on protected endpoints
3. **Refresh**: Automatic token renewal via secure refresh mechanism
4. **Logout**: Token invalidation and cookie clearing

### **Authorization Model**
- **Endpoint Protection**: Method-level `@PreAuthorize` annotations
- **Role Hierarchy**: ADMIN > USER > API_CLIENT permissions
- **Account Status**: Real-time validation of enabled/locked status
- **Failed Login Tracking**: Automatic account locking after failed attempts

### **Data Protection**
- **Credentials**: AES-GCM-256 encryption at rest
- **Passwords**: BCrypt hashing with salt
- **Tokens**: Secure generation and validation
- **Transport**: HTTPS required for production
- **Audit**: Complete logging of sensitive operations

## Configuration Profiles

### **Development Profile** (`application-dev.properties`)
- **Database**: File-based H2 with PostgreSQL compatibility
- **Security**: Development JWT secrets and encryption keys
- **Logging**: SQL statement logging and query formatting
- **H2 Console**: Enabled for database inspection

### **Production Profile** (`application-prod.properties`)
- **Database**: PostgreSQL with connection pooling
- **Security**: Environment-variable-based secrets
- **Monitoring**: Enhanced actuator endpoints
- **Performance**: Optimized JPA and connection settings

## API Architecture

### **RESTful Design**
- **Authentication**: `/api/auth/*` - Login, logout, refresh, validation
- **Resources**: `/api/{resource}` - Standard CRUD operations
- **Health**: `/healthz` - Application health monitoring
- **Security**: Role-based endpoint access control

### **Request/Response Pattern**
- **DTOs**: Separate request/response objects with validation
- **Error Handling**: Consistent error response format
- **Validation**: Bean validation with custom business rules
- **Serialization**: JSON with Jackson configuration

## Service Layer Architecture

### **Business Logic Organization**
- **Interface-Based Design**: Service contracts with implementations
- **Transaction Management**: Proper `@Transactional` boundaries
- **Exception Handling**: Custom business exceptions
- **Validation**: Business rule enforcement
- **Orchestration**: Cross-domain operation coordination

### **Data Access Pattern**
- **Repository Layer**: Spring Data JPA with custom query methods
- **Entity Management**: JPA lifecycle hooks for encryption
- **Query Optimization**: Strategic use of indexes and relationships
- **Connection Management**: Automatic connection pooling

## Implementation Status

### **Fully Implemented** ✅
- Authentication and authorization system
- User management with RBAC
- Platform integration framework
- Credential encryption and key management
- Product catalog and listing management
- Order processing and buyer management
- Service layer with business logic
- Database schema with proper relationships
- Configuration and environment management

### **Next Development Phase** 🚧
- **Platform Client Implementations**: eBay, Amazon, etc. API clients
- **Batch Processing**: Spring Batch jobs for synchronization
- **Caching Strategy**: Redis or in-memory caching
- **API Documentation**: Swagger/OpenAPI integration
- **Monitoring Enhancement**: Metrics and alerting
- **Rate Limiting**: API throttling and protection