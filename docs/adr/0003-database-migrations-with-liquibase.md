# 0003. Database migrations with Liquibase

- **Status:** Accepted
- **Date:** 2026-03-04

## Context

The database schema must evolve together with the code, identically on every environment
(developer machines, CI, production). Letting Hibernate generate the schema (`ddl-auto: update`)
is unpredictable and cannot express data migrations or rollbacks.

## Options considered

1. **Hibernate `ddl-auto`** — zero effort, but no control, no history, dangerous in production.
2. **Flyway** — simple, SQL-first, popular.
3. **Liquibase** — changesets with explicit ids and authors, rollback support, contexts; widely used in our region.

## Decision

We use Liquibase with **formatted SQL** changesets: plain SQL is easy to read and review, and Liquibase
tracks what has been applied. Hibernate runs with `ddl-auto: validate` to catch mismatches between
entities and the schema at startup.

## Consequences

- Every schema change is a reviewed file in git.
- Applied changesets must never be modified (Liquibase checks checksums and fails on startup).
- Developers need to write SQL by hand and think about backward compatibility.
