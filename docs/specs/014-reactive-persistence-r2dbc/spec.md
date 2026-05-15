# Lab Specification: LAB-014: Reactive Persistence with R2DBC [AUDITED]

**Feature Branch**: `014-reactive-persistence-r2dbc`
**Created**: 2026-05-04
**Status**: Ready
**Syllabus Section**: Level 3: Enterprise Reactive Services (Spring WebFlux)

## Syllabus Alignment *(mandatory)*

- **Concept**: Non-blocking database access using R2DBC and Spring Data R2DBC.
- **Prerequisites**: LAB-011 (WebFlux Basics), LAB-005 (Essential Operators).
- **Learning Objectives**:
  - LO-001: Configure R2DBC connectivity for PostgreSQL using Testcontainers.
  - LO-002: Implement CRUD operations using `ReactiveCrudRepository`.
  - LO-003: Execute custom queries via `DatabaseClient`.
  - LO-004: Implement reactive transaction management using `@Transactional`.
  - LO-005: Handle PostgreSQL-specific features like `JSONB` reactively.
  - LO-006: Integrate `r2dbc-proxy` for advanced query observability and auditing.
  - LO-007: Manage schema migrations using a reactive-compatible approach.
  - LO-008: Evaluate architectural trade-offs between R2DBC and JDBC + Virtual Threads (Project Loom).
  - LO-009: Understand the incompatibility of JPA/Hibernate with the reactive model.
  - LO-010: Master the use of `R2dbcEntityTemplate` for programmatic query construction.
  - LO-011: Define and contrast the "Basic ORM" approach vs. traditional full-featured ORMs.

## Interactive Scenarios & Validation *(mandatory)*

### Scenario 1 - Reactive Schema & Seeding (P1)
Configure a `ConnectionFactoryInitializer` to execute `schema.sql` and `data.sql` upon application startup.

**Validation (Automated Test)**: Verify that the `Product` table exists and contains the initial seeded records before running any other tests.

---

### Scenario 2 - Reactive Repository CRUD (P1)
Implement a `ProductRepository` extending `ReactiveCrudRepository<Product, Long>`. Create a REST controller that exposes:
- `GET /api/products`: Returns a `Flux<Product>`.
- `POST /api/products`: Saves a product and returns `Mono<Product>`.

**Validation (Automated Test)**: Use `WebTestClient` to perform a POST and then a GET, asserting the product is correctly persisted and retrieved.

---

### Scenario 3 - Advanced Queries with DatabaseClient & R2dbcEntityTemplate (P2)
Implement:
1. A custom search method using `DatabaseClient` for raw SQL flexibility (partial name search).
2. A custom search method using `R2dbcEntityTemplate` for type-safe programmatic criteria (price range filter).

**Validation (Automated Test)**: Verify that both templates correctly retrieve matching items from the database.

---

### Scenario 4 - The Transactional Integrity (P2)
Create a service method `purchaseProduct(Long id, int quantity)` that decrements stock and creates an order record. If order creation fails, the stock must be rolled back.

**Validation (Automated Test)**: Trigger a failure and verify the database state using Testcontainers.

---

### Scenario 5 - JSONB Document Handling (P2)
Store a "metadata" field in the `Product` entity as a PostgreSQL `JSONB` column. Query and update specific JSON fields reactively.

**Validation (Automated Test)**: Persist a product with metadata, then query products filtering by a value inside the JSONB column.

---

### Scenario 6 - Query Observability with R2DBC Proxy (P3)
Configure `r2dbc-proxy` to wrap the connection factory and log all executed SQL statements and their execution times.

**Validation (Manual/Log Audit)**: Verify that the console output shows the intercepted SQL queries in a non-blocking way.

## Educational Requirements *(mandatory)*

### Concepts to Explain
- **EX-001**: Why JDBC is blocking and why we need R2DBC (HikariCP vs. ConnectionFactory).
- **EX-002**: The role of `ConnectionFactory` vs `DataSource` and the Netty/TCP mechanism.
- **EX-003**: Reactive Transactions: How the Reactor Context propagates the transaction state.
- **EX-004**: Incompatibility with JPA/Hibernate and the shift to basic ORM/DatabaseClient.

### Technical Requirements
- **TR-001**: Use `spring-boot-starter-data-r2dbc`.
- **TR-002**: Use `io.r2dbc:r2dbc-postgresql` as the main driver.
- **TR-003**: Use `testcontainers` and `testcontainers-postgresql` for integration testing.
- **TR-004**: Use `r2dbc-proxy` for query logging.

## Success Criteria *(measurable outcomes)*
- **SC-001**: Learner persists data without any blocking `Thread.sleep()` or JDBC calls.
- **SC-002**: Learner successfully rolls back a multi-step database operation.
- **SC-003**: Learner provides Traceable Implementation links for all scenarios to connect theory directly to the Java implementation.
- **SC-004**: "Self-Assessment" section utilizes collapsible UI blocks for immediate feedback.

## Assumptions
- In-memory H2 database is sufficient for the lab.
- Spring Boot 3.2+ is used.
