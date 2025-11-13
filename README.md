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

## Docker Dev Stack

Spin up the API, Postgres, Go marketplace stub, and an optional frontend dev server with a consistent `docker compose` workflow.

### Prerequisites

- Docker Desktop (or equivalent) with Compose v2
- GNU Make

### Quick Start

1. Copy the sample env files: `cp env/.sample/*.env env/`
2. Start everything: `make dev-up`
3. Follow logs: `make dev-logs` (or `make dev-logs SERVICE=api`)
4. Stop containers: `make dev-down` (use `make dev-clean` to also drop volumes)

The Makefile also provides `api-shell`, `marketplace-shell`, `api-image`, `marketplace-image`, and `compose-config` helpers.

### Services & Ports

| Service | Description | Host Port | Notes |
| --- | --- | --- | --- |
| `api` | Spring Boot app (`Dockerfile.api`) | 8080 | Talks to Postgres at `jdbc:postgresql://db:5432/hopper` |
| `db` | PostgreSQL 15 + Flyway bootstrap | 5432 | Data stored in the `pgdata` volume |
| `marketplace` | Go marketplace stub | 8090 | Health endpoint at `/v1/health` |
| `frontend` | Vite dev server placeholder | 5173 | Opt-in profile `--profile frontend` |
| `seed` | Flyway CLI for manual migrations | n/a | Run as needed: `docker compose --profile seed run --rm seed` |

### Environment Files

- Samples live in `env/.sample/*.env`; working copies belong in `env/*.env` (gitignored).
- `env/api.env` holds API secrets and DB credentials, `env/db.env` configures the Postgres container, `env/marketplace.env` covers the Go stub, and `env/frontend.env` stores future Vite vars.
- Use `scripts/wait-for-db.sh db 5432` if you need to gate other tooling until Postgres is healthy.

### Overrides & Profiles

- Drop a local `docker-compose.override.yml` when you need to remap ports or add bind mounts; Compose will auto-merge it.
- Optional services (`frontend`, `seed`) are protected by profiles so they only run when explicitly requested: `docker compose --profile frontend up`.
- Database state lives in the `pgdata` named volume; reset it with `make dev-clean` when you need a blank slate.

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
