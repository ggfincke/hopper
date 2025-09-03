# Hopper

Cross-platform inventory management for ecommerce sellers.

## Overview

Hopper is designed to centralize inventory management across multiple ecommerce platforms, enabling sellers to efficiently manage listings, sync stock levels, and track sales from a single dashboard. Built for sellers on eBay, TCGPlayer, StockX, Facebook Marketplace, and more.

**Note: All features listed below represent intended functionality. This project is in early development.**

## Planned Features

- **Cross-platform inventory sync** - Maintain accurate stock levels across all marketplaces
- **Centralized listing management** - Create and update listings from one interface
- **Price optimization** - Dynamic pricing based on market conditions and competition
- **Order fulfillment tracking** - Monitor orders from purchase to delivery
- **Analytics and reporting** - Sales performance, profit margins, and market insights
- **Automated repricing** - Keep listings competitive with market changes
- **Low stock alerts** - Never oversell with intelligent inventory warnings

## Supported Platforms (Planned)

- eBay
- TCGPlayer
- StockX
- Facebook Marketplace
- Additional platforms coming soon

## Tech Stack

- **Backend**: Spring Boot 3.5.5 + Kotlin 1.9.25
- **Database**: PostgreSQL with Flyway migrations
- **Security**: Spring Security
- **Batch Processing**: Spring Batch for data synchronization
- **Monitoring**: Spring Boot Actuator

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

# Run the application
./gradlew bootRun
```

## Development

```bash
./gradlew build       # Build project
./gradlew bootRun     # Run application
./gradlew test        # Run tests
./gradlew check       # Run all verification tasks
```

## Configuration

Database configuration and other settings can be found in `src/main/resources/application.properties`.

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