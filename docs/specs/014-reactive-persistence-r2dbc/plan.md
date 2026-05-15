# Implementation Plan: LAB-014: Reactive Persistence with R2DBC [READY]

**Branch**: `014-reactive-persistence-r2dbc` | **Date**: 2026-05-04
**Input**: Specification from `docs/specs/014-reactive-persistence-r2dbc/spec.md`

## Summary

The learner will transition from web-layer reactivity to full end-to-end reactive persistence. This lab focuses on removing the "Blocking DB" bottleneck using R2DBC, exploring repositories, manual query composition, and transactional integrity.

## Phase 1: Infrastructure & Data Setup
1. **Scaffold**: Create `labs/014-reactive-persistence-r2dbc/`.
2. **Workspace Registration**: Add the lab to the root `pom.xml`.
3. **Database Configuration**: Configure PostgreSQL R2DBC with `testcontainers`.
4. **Observability**: Set up `r2dbc-proxy` for SQL auditing.

## Phase 2: Reactive Repositories & CRUD
1. **Instructional Path**:
   - Defining `@Table` entities.
   - Implementing `ReactiveCrudRepository`.
   - Building a CRUD REST Controller.
2. **Validation**: `WebTestClient` tests for end-to-end persistence flow.

## Phase 3: Advanced Operations & Templates
1. **Instructional Path**:
   - Using `DatabaseClient` for custom mapping and raw SQL.
   - Using `R2dbcEntityTemplate` for programmatic criteria.
   - Applying `@Transactional` and understanding its non-blocking nature.
   - Implementing pagination logic.
2. **Validation**: Test cases for template-based searches and rollback scenarios.

## Phase 4: Documentation & Dissection
1. **CONCEPT.md**: Compare JDBC vs R2DBC. Explain "Basic ORM" concepts.
2. **README.md**: Command Dissections for `ReactiveCrudRepository`, `DatabaseClient`, and `R2dbcEntityTemplate`.

## Phase 5: Constitution v0.2.7 Refinement
1. **Instructional Path**:
   - Add Traceable Implementation links.
   - Inject the "Self-Assessment" section.
2. **Validation**: Verify relative links.

## Phase 6: Architectural Modularity (Refinement)
1. **Instructional Path**:
   - Refactor from flat package to modular packages (`controller`, `service`, `repository`, `model`, `config`).
   - Move main class to the root package.
2. **Validation**: `mvn test` to ensure stability post-restructuring.

## Constitution Compliance Check
- [ ] No `.sh` wrapper scripts.
- [ ] Code comments explicitly describe reactive DB signal flow.
- [ ] Language: English.
- [ ] **Dependency Governance**: Centralized in root parent POM.

## Open Questions
- Should we include a "Project Reactor" version of a Migration tool (like Flyway/Liquibase) or keep it simple with `ConnectionFactoryInitializer`?
- Should we demonstrate the `r2dbc-proxy` for query logging?
