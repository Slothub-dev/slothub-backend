# 0004. Testing strategy

- **Status:** Accepted
- **Date:** 2026-03-10

## Context

We want fast feedback during development and high confidence before merging.
H2 and other in-memory databases behave differently from PostgreSQL and hide real problems.

## Decision

Two levels of automated tests:

| Level | Naming | Runs with | What it checks |
|---|---|---|---|
| Unit | `*Test.java` | Surefire, `./mvnw test` | business logic in isolation, dependencies mocked with Mockito |
| Integration | `*IT.java` | Failsafe, `./mvnw verify` | HTTP → service → real PostgreSQL (Testcontainers) |

- Integration tests extend `AbstractIntegrationTest`: full Spring context, MockMvc, a PostgreSQL container
  shared across test classes. Each test runs in a transaction that is rolled back.
- Business rules (prices, availability, statuses) must have unit tests.
- Every endpoint must have at least one integration test for the happy path and one for an error case.
- CI runs `./mvnw verify` on every pull request.

## Consequences

- Integration tests require Docker locally.
- Tests are close to production behavior; no H2-specific surprises.
- Transactional tests cannot catch problems that only appear with multiple concurrent transactions —
  such cases need dedicated tests without rollback.
