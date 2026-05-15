# Tasks: LAB-014: Reactive Persistence with R2DBC [READY]

**Input**: Design documents from `docs/specs/014-reactive-persistence-r2dbc/`
**Prerequisites**: plan.md, spec.md.

**Validation Goal**: Build a fully non-blocking persistence layer that survives transactional failures and handles high-concurrency connection pooling.

## Phase 1: Infrastructure & Project Setup
- [x] T001 Create `labs/014-reactive-persistence-r2dbc` directory structure.
- [x] T002 Update root `pom.xml` module registration.
- [x] T003 Configure `pom.xml` dependencies (`spring-boot-starter-data-r2dbc`, `r2dbc-postgresql`, `testcontainers`, `r2dbc-proxy`).
- [x] T015 Create `Dockerfile` and `docker-compose.yml` for local deployment.

## Phase 2: Scenario Implementation (TDD)
- [x] T004 Implement Scenario 1: Setup `PostgreSQLContainer` in test and verify connectivity.
- [x] T005 Configure R2DBC connectivity for PostgreSQL using Testcontainers.
- [x] Implement CRUD operations using `ReactiveCrudRepository`.
- [x] Execute custom queries via `DatabaseClient`.
- [x] Implement reactive transaction management using `@Transactional`.
- [x] Handle PostgreSQL-specific features like `JSONB` reactively.
- [x] Integrate `r2dbc-proxy` for advanced query observability and auditing.
- [/] Refine pedagogical content: JDBC vs R2DBC (Netty/TCP) and JPA/Hibernate incompatibility.

## Phase 3: Documentation & Educational Content
- [x] T009 Create `CONCEPT.md` detailing the R2DBC event-loop model.
- [x] T010 Create `README.md` with step-by-step instructions and Command Dissections.
- [x] T011 Add code comments explaining the "Non-blocking" nature of `@Transactional`.

## Phase 4: Final Certification
- [x] T012 Verify all tests pass with `mvn test`.
- [x] T013 Verify compliance with the Constitution.
- [x] T014 Update `docs/syllabus.md` status to `AUDITED`.

## Phase 5: Constitution v0.2.7 Refinement
- [x] T015 Update `spec.md` and `plan.md` with Virtual Threads and Traceability goals.
- [x] T016 Add Virtual Threads (Project Loom) conceptual comparison to `CONCEPT.md`.
- [x] T017 Add Traceable Implementation relative links to all scenarios in `README.md`.
- [x] T018 Add the missing Self-Assessment block using collapsible `<details>` tags in `README.md`.
- [x] T019 Execute `mvn test` to ensure stability post-refinement.

## Phase 6: Constitution v0.2.8 Refinement (Podman Support)
- [x] T020 Add Podman alternative commands to `README.md` for manual deployment and replica set initialization.
- [x] T021 Update `README.md` prerequisites to include Podman.
- [x] T022 Verify that Testcontainers works with Podman environment (if applicable).
