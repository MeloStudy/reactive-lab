# Lab Specification: LAB-011: Reactive Web with Spring WebFlux [READY]

**Feature Branch**: `011-reactive-web-webflux`
**Created**: 2026-05-04
**Status**: Ready
**Syllabus Section**: Level 3: Enterprise Reactive Services (Spring WebFlux)

## Syllabus Alignment *(mandatory)*

- **Concept**: Building non-blocking web services with Spring WebFlux.
- **Prerequisites**: LAB-010 (Testing & Debugging), LAB-007 (Threading).
- **Learning Objectives**:
  - LO-001: Build reactive REST APIs using the annotation-based model (`@RestController`).
  - LO-002: Build functional APIs using `RouterFunction` and `HandlerFunction`.
  - LO-003: Implement real-time data streaming using Server-Sent Events (SSE).
  - LO-004: Stream large datasets efficiently using NDJSON (`application/x-ndjson`).
  - LO-005: Apply cross-cutting concerns using functional **Filters** (`HandlerFilterFunction`).
  - LO-006: Implement global reactive error handling strategies.
  - LO-007: Understand the Netty-based event loop architecture vs. Servlet containers.

## Interactive Scenarios & Validation *(mandatory)*

### Scenario 1 - The Reactive Controller (P1)

Create a standard REST API that fetches "Stock Prices" from a reactive service. The learner must ensure no blocking calls happen in the controller.

**Validation (Automated Test)**: Use `WebTestClient` to verify that the endpoint returns `application/json` and handles 404s reactively.

---

### Scenario 2 - The Functional Router (P2)

Re-implement the same Stock Price API but using the Functional Programming model. Define a `RouterFunction` that routes requests to a `StockHandler`.

**Validation (Automated Test)**: Use `WebTestClient` to hit the functional endpoint and verify the same business logic.

---

### Scenario 3 - The Live Stock Ticker (P2)

Implement an endpoint that streams price updates every second using Server-Sent Events (SSE).

**Validation (Automated Test)**: Use `WebTestClient` to consume the stream and verify that multiple `onNext` signals are received over time.

---

### Scenario 4 - The Bulk Export (P3)

Expose a large dataset of transactions as a Streaming JSON flux. Use `application/x-ndjson` to ensure the client can process elements one by one as they arrive.

**Validation (Automated Test)**: Verify the `Content-Type` is `application/x-ndjson` and that elements are received incrementally.

---

### Scenario 5 - The Pipeline Guard (P3)

Implement a `HandlerFilterFunction` that logs the execution time of every functional request and adds a custom security header to the response. Also, implement a global error handler for `StockNotFoundException`.

**Validation (Automated Test)**: Assert that the custom header is present in the response and that errors are returned as JSON with proper status codes.

---

## Educational Requirements *(mandatory)*

### Concepts to Explain

- **EX-001**: Thread-per-request (Servlet) vs. Event Loop (Netty).
- **EX-002**: Why `Mono<T>`/`Flux<T>` are necessary return types in WebFlux.
- **EX-003**: Functional vs. Annotated: When to use which?

### Technical Requirements

- **TR-001**: Use `spring-boot-starter-webflux`.
- **TR-002**: Validation MUST use `WebTestClient`.
- **TR-003**: No blocking database calls (simulated with `delayElement`).

## Success Criteria *(measurable outcomes)*

- **SC-001**: Learner creates a streaming endpoint that stays open and pushes data without closing.
- **SC-002**: Learner identifies why a `List<T>` return type would block whereas `Flux<T>` does not.

## Assumptions

- Spring Boot 3.2+ and Java 21+ are used.
