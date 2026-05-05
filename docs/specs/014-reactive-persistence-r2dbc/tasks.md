# Tasks: LAB-014: Reactive Persistence with R2DBC [READY]

**Input**: Design documents from `docs/specs/014-reactive-persistence-r2dbc/`
**Prerequisites**: plan.md, spec.md.

**Validation Goal**: Build a fully non-blocking persistence layer that survives transactional failures and handles high-concurrency connection pooling.

## Phase 1: Infrastructure & Project Setup
- [ ] T001 Create `labs/014-reactive-persistence-r2dbc` directory structure.
- [ ] T002 Update root `pom.xml` module registration.
- [ ] T003 Configure `pom.xml` dependencies (`spring-boot-starter-data-r2dbc`, `r2dbc-postgresql`, `testcontainers`, `r2dbc-proxy`).

## Phase 2: Scenario Implementation (TDD)
- [ ] T004 Implement Scenario 1: Setup `PostgreSQLContainer` in test and verify connectivity.
- [ ] T005 Implement Scenario 2: Create `Product` entity with `JSONB` support and CRUD tests.
- [ ] T006 Implement Scenario 3: Custom search using `DatabaseClient` for JSONB fields.
- [ ] T007 Implement Scenario 4: Transactional rollback test using Testcontainers database.
- [ ] T008 Implement Scenario 5: Configure and verify `r2dbc-proxy` query logging.

## Phase 3: Documentation & Educational Content
- [ ] T009 Create `CONCEPT.md` detailing the R2DBC event-loop model.
- [ ] T010 Create `README.md` with step-by-step instructions and Command Dissections.
- [ ] T011 Add code comments explaining the "Non-blocking" nature of `@Transactional`.

## Phase 4: Final Certification
- [ ] T012 Verify all tests pass with `mvn test`.
- [ ] T013 Verify compliance with the Constitution.
- [ ] T014 Update `docs/syllabus.md` status to `READY`.
