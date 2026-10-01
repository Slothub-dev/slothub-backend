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

### Local environment

#### Stop and reset

```bash
# Stop the containers, keep the data
docker compose stop

# Stop and remove the containers together with the database volume.
# WARNING: this deletes all local data.
docker compose down -v
```

#### Port conflicts

If port `5432` or `8080` is already taken on your machine, override the ports with environment variables
instead of editing files in the repository. Create a `.env` file in the project root
(it is listed in `.gitignore` and must not be committed):

```properties
# PostgreSQL port on your machine (used by docker compose)
POSTGRES_PORT=5433
# The application must connect to the same port
DB_URL=jdbc:postgresql://localhost:5433/slothub
# Application HTTP port
SERVER_PORT=8081
```

- Docker Compose reads `.env` automatically.
- Spring Boot does not read `.env` by itself. In IntelliJ IDEA open **Run → Edit Configurations**, select the
  application configuration and add the `.env` file in the **Environment variables** field.
  When running from a terminal, set the variables in the shell (PowerShell: `$env:SERVER_PORT="8081"`).
- Any Spring property can be overridden this way: `server.port` becomes `SERVER_PORT`,
  `spring.jpa.show-sql` becomes `SPRING_JPA_SHOW_SQL`, and so on.

#### Running from IntelliJ IDEA

1. Start PostgreSQL: `docker compose up -d`.
2. Open `src/main/java/com/slothub/SlotHubApplication.java` and click the green Run icon in the gutter next to the class.

Tests:

- **All tests:** right-click `src/test/java` → **Run 'All Tests'**. Integration tests (`*IT`) require Docker to be running.
- **Through Maven:** in the **Maven** tool window run **Lifecycle → test** (unit tests only)
  or **Lifecycle → verify** (all tests).

#### Troubleshooting

- **IntelliJ IDEA shows `Unresolved dependency`.** Force Maven to re-download dependencies with
  `./mvnw -U clean test-compile` (Windows: `mvnw.cmd -U clean test-compile`),
  then click **Reload All Maven Projects** in the **Maven** tool window.

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
