# 0002. Modular monolith, package by feature

- **Status:** Accepted
- **Date:** 2026-03-02

## Context

SlotHub is an early-stage product with a small team. Requirements change often, and we need to ship fast.
Microservices would add operational overhead (deployment, network calls, distributed transactions)
that we cannot afford yet. At the same time, we want to keep the codebase easy to split later.

## Options considered

1. **Microservices from day one** — independent deployments, but high operational cost and complexity.
2. **Monolith, package by layer** (`controller/`, `service/`, `repository/`) — familiar, but features get
   scattered across the whole codebase and boundaries between them erode.
3. **Monolith, package by feature** (`venue/`, `space/`, `booking/`) — one deployable unit, while each feature
   keeps its code together and has a clear boundary.

## Decision

We build a single Spring Boot application organized **by feature**. Inside each feature package we use layers
(entity, repository, service, controller, mapper, dto).

Rules:

- Features talk to each other through **services**, not through each other's repositories.
- Controllers never return entities, only DTOs.
- Shared code goes to `common` only if it is used by at least two features.

## Consequences

- Simple deployment and local development.
- A feature can be extracted into a separate service later with limited effort.
- Boundaries are enforced by convention only; reviewers must watch for cross-feature coupling.
