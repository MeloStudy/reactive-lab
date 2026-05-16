# Lab Specification: LAB-015: Reactive Persistence with MongoDB [AUDITED]

**Feature Branch**: `015-reactive-persistence-mongodb`
**Created**: 2026-05-04
**Status**: AUDITED
**Syllabus Section**: Level 3: Enterprise Reactive Services (Spring WebFlux)

## Syllabus Alignment *(mandatory)*

- **Concept**: Non-blocking access to NoSQL document storage with high-throughput streaming capabilities.
- **Prerequisites**: LAB-011 (WebFlux Basics), LAB-008 (Programmatic Streams - Sinks).
- **Learning Objectives**:
  - LO-001: Understand the hierarchy from `ReactiveMongoClient` to `ReactiveMongoTemplate` and `ReactiveMongoRepository`.
  - LO-002: Configure a reactive MongoDB connection using Testcontainers.
  - LO-003: Implement CRUD operations with `ReactiveMongoRepository`.
  - LO-004: Stream real-time data using **Tailable Cursors** on Capped Collections.
  - LO-005: Implement event-driven triggers using **Change Streams**.
  - LO-006: Handle binary data streams (upload and download) using **GridFS** reactively.
  - LO-007: Execute complex document aggregations in a non-blocking way.
  - LO-008: Apply backpressure handling strategies (`onBackpressureDrop`) to native database streams.
  - LO-009: Evaluate architectural trade-offs between Reactive MongoDB push-streams vs Virtual Thread polling.

## Interactive Scenarios & Validation *(mandatory)*

### Scenario 1 - The Infinite Log Stream (P1)
Configure a **Capped Collection** `system_logs`. Implement a service that inserts random logs and an endpoint `GET /api/logs/stream` that returns a `Flux<LogEntry>` using a **Tailable Cursor**.

**Validation (Automated Test)**: Start the stream, insert 5 logs, and verify the `StepVerifier` receives all 5 signals without the stream closing.

---

### Scenario 2 - Real-time Change Notifications (P1)
Implement a **Change Stream** watcher on the `Products` collection. Whenever a product price changes, push a message to a `Sinks.Many` and expose it via SSE.

**Validation (Automated Test)**: Update a product and verify the notification is emitted via the sink.

---

### Scenario 3 - Reactive File Storage (GridFS) (P2)
Implement a file upload/download service using `ReactiveGridFsTemplate`. The file must be streamed as `DataBuffer` chunks.

**Validation (Automated Test)**: Upload a 5MB random byte array and download it, asserting that the checksum matches and no blocking calls occurred.

---

### Scenario 4 - Reactive Analytics Pipeline (P2)
Perform a 3-stage aggregation (match, group, sort) on a `Sales` collection to find the top 5 selling categories.

**Validation (Automated Test)**: Seed sales data and verify the aggregation result matches expected values using appropriate AssertJ methods (`containsEntry`).

---

### Scenario 5 - Backpressure Simulation (P1)
Demonstrate backpressure by simulating a slow consumer interacting with a fast stream of MongoDB change events or logs. Use `.onBackpressureDrop()` or `.onBackpressureBuffer()` to handle the overflow gracefully.

**Validation (Automated Test)**: Insert logs at a high frequency while reading with a simulated delay. Verify that dropped items do not cause out-of-memory errors or application crashes.

## Educational Requirements *(mandatory)*

### Concepts to Explain
- **EX-001**: Difference between R2DBC (Relational) and Reactive MongoDB (Document).
- **EX-002**: The Reactive MongoDB Client Hierarchy (`ReactiveMongoClient` -> `ReactiveMongoTemplate` -> `ReactiveMongoRepository`).
- **EX-003**: What is a Capped Collection and why it enables Tailable Cursors.
- **EX-004**: Change Streams: How MongoDB implements the "Observer" pattern at the database level.
- **EX-005**: Backpressure in a database context: Push model implications and drop strategies.

### Technical Requirements
- **TR-001**: Use `spring-boot-starter-data-mongodb-reactive`.
- **TR-002**: Use `testcontainers-mongodb` for validation.
- **TR-003**: Use `Sinks` for Change Stream propagation.

## Success Criteria *(measurable outcomes)*
- **SC-001**: Student creates an "infinite" HTTP stream from a database cursor.
- **SC-002**: Student successfully handles file binary streams without memory overflows.
- **SC-003**: Learner provides Traceable Implementation links for all scenarios to connect theory directly to the Java implementation.
- **SC-004**: "Self-Assessment" section utilizes collapsible UI blocks for immediate feedback.
