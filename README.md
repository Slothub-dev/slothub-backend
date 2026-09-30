# SlotHub Backend

[![CI](https://github.com/Slothub-dev/slothub-backend/actions/workflows/ci.yml/badge.svg)](https://github.com/Slothub-dev/slothub-backend/actions/workflows/ci.yml)

SlotHub is an online booking platform for sports courts, meeting rooms and coworking desks.
Venues (clubs, business centers) publish their spaces, customers book time slots.

This repository contains the backend REST API.

## Tech stack

- Java 21, Spring Boot 3.5 (Web, Data JPA, Validation, Actuator)
- PostgreSQL 17, Liquibase
- MapStruct, Lombok
- springdoc-openapi (Swagger UI)
- JUnit 5, Mockito, Testcontainers
- Maven (via wrapper), Docker Compose, GitHub Actions

## Getting started

### Prerequisites

- JDK 21 or newer
- Docker (Docker Desktop on Windows/macOS)

### Run locally

```bash
# 1. Start PostgreSQL
docker compose up -d

# 2. Start the application (Windows: mvnw.cmd spring-boot:run)
./mvnw spring-boot:run
```

Liquibase applies all migrations on startup.

| What | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI spec | http://localhost:8080/v3/api-docs |
| Health check | http://localhost:8080/actuator/health |

Database connection can be overridden with `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` environment variables.

### Tests

```bash
# Unit tests only (fast, no Docker needed)
./mvnw test

# Unit + integration tests (requires running Docker)
./mvnw verify
```

- `*Test.java` — unit tests, run by Surefire.
- `*IT.java` — integration tests with real PostgreSQL in Testcontainers, run by Failsafe.

## Project structure

```
src/main/java/com/slothub
├── common/     shared exceptions and web error handling
├── venue/      venues: clubs, business centers, coworkings
├── space/      bookable spaces inside a venue
└── booking/    bookings of spaces
```

The code is organized by feature (see [ADR-0002](docs/adr/0002-modular-monolith-package-by-feature.md)).
Each feature package contains its entity, repository, service, controller, mapper and DTOs.

Database migrations live in `src/main/resources/db/changelog/changes`.

## Contributing

Read [CONTRIBUTING.md](CONTRIBUTING.md) before opening your first pull request.
Architecture decisions are recorded in [docs/adr](docs/adr).
