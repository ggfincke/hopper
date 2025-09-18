# Hopper

Cross-platform inventory management for ecommerce sellers.

## Overview

Hopper is designed to centralize inventory management across multiple ecommerce platforms, enabling sellers to efficiently manage listings, sync stock levels, and track sales from a single dashboard. Built for sellers on eBay, TCGPlayer, StockX, Facebook Marketplace, and more.

**Note: All features listed below represent intended functionality. This project is in early development.**

## Planned Features

- **Platform client implementations** - Direct API integration with eBay, Amazon, etc.
- **Cross-platform inventory sync** - Real-time stock level synchronization
- **Price optimization** - Dynamic pricing based on market conditions
- **Analytics and reporting** - Sales performance and profit margin insights
- **Automated repricing** - Competitive pricing algorithms
- **Batch processing** - Background synchronization jobs

## Supported Platforms (Planned)

- eBay
- TCGPlayer
- StockX
- Facebook Marketplace
- Additional platforms coming soon

## Tech Stack

- **Backend**: Spring Boot 3.5.5 + Java (21/24)
- **Database**: PostgreSQL (production) / H2 (development) with Flyway migrations
- **Security**: Spring Security with JWT authentication
- **Encryption**: AES-GCM-256 with PBKDF2 key derivation
- **Build Tool**: Gradle with wrapper
- **Monitoring**: Spring Boot Actuator with health checks
- **Validation**: Bean validation with Hibernate Validator

## Getting Started

### Prerequisites

- Java 21 or 24
- PostgreSQL
- Gradle (wrapper included)

### Installation

```bash
# Clone the repository
git clone <repository-url>
cd hopper

# Build the project
./gradlew build

# Run the application (development mode with H2)
./gradlew bootRun
```

### Environment Configuration

**Development**: Uses H2 database with default settings

**Production**: Requires these environment variables:
```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/hopper
export DATABASE_USERNAME=hopper_user
export DATABASE_PASSWORD=your_db_password
export JWT_SECRET=your_jwt_secret_key_minimum_256_bits
export CREDENTIAL_MASTER_KEY=your_32_plus_character_encryption_key
```

## Development

```bash
./gradlew build       # Build project
./gradlew bootRun     # Run application
./gradlew test        # Run tests
./gradlew check       # Run all verification tasks
```

## Configuration

The application uses profile-based configuration:
- `application.properties` - Base configuration
- `application-dev.properties` - Development (H2 database)
- `application-prod.properties` - Production (PostgreSQL)

**Key configuration areas:**
- Database connections and JPA settings
- JWT token expiration and security settings
- Credential encryption parameters
- CORS and security policies

## Project Status

🚧 **Early Development** - Core functionality is being implemented. This README describes the intended feature set.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Run tests: `./gradlew test`
5. Submit a pull request

## License

TBD