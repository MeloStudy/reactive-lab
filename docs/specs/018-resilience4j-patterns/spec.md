# Lab Specification: LAB-018: Advanced Resilience Patterns (Resilience4j) [AUDITED]

**Feature Branch**: `018-resilience4j-patterns`
**Created**: 2026-05-18
**Status**: Audited
**Syllabus Section**: Level 4: Resilient & Event-Driven Systems

## Syllabus Alignment *(mandatory)*

- **Concept**: Integrating Resilience4j patterns (Circuit Breaker, Bulkhead, Rate Limiter, Time Limiter) within reactive Project Reactor WebFlux pipelines.
- **Prerequisites**: LAB-011 (WebFlux Basics), LAB-012 (WebClient), LAB-013 (Error Handling Basics).
- **Learning Objectives**:
  - LO-001: Integrate Resilience4j with Reactor using the reactive bridge operators (`transformDeferred`).
  - LO-002: Master Circuit Breaker states (CLOSED, OPEN, HALF_OPEN) and trip conditions (failure rates, slow call thresholds).
  - LO-003: Configure Semaphore-based Bulkheads to isolate execution thread limits and protect server capacity.
  - LO-004: Apply Rate Limiters to throttle inbound client requests based on custom execution quotas.
  - LO-005: Integrate Time Limiters to guard sluggish reactive streams and implement fallback handlers.
  - LO-006: Design custom Fallback handlers to recover from tripped state exceptions (e.g., `CallNotPermittedException`).

## Interactive Scenarios & Validation *(mandatory)*

### Scenario 1 - Reactive Circuit Breaker (Priority: P1)
Create an endpoint `/api/resilient/orders/{id}` that interacts with a shaky downstream order database. Apply a `CircuitBreaker` using the `transformDeferred` operator. Configure it with a sliding window of 10 requests, a failure rate threshold of 50%, and an automatic transition duration from OPEN to HALF_OPEN of 2 seconds. When the circuit is OPEN (throwing `CallNotPermittedException`), it must fall back gracefully to a mock default Order.

**Validation (Automated Test)**: Use `WebTestClient` to make 10 requests where 6 fail. Assert that the 11th request is immediately blocked with `CallNotPermittedException` (HTTP 503) or falls back to the default order successfully. Verify that after 2 seconds, the circuit transitions to HALF_OPEN and accepts test probes.

**Acceptance Scenarios**:
1. **Given** a failing downstream publisher, **When** failure threshold exceeds 50%, **Then** the Circuit Breaker transitions to OPEN and immediately rejects incoming calls without executing the service pipeline.
2. **Given** an OPEN circuit, **When** the wait duration in open state expires, **Then** it transitions to HALF-OPEN and permits a limited number of trial executions.

---

### Scenario 2 - Concurrent Bulkhead Isolation (Priority: P1)
Create an endpoint `/api/resilient/report` that simulates a resource-heavy report generation task. Protect this resource using a Semaphore-based `Bulkhead` configured to permit a maximum of 2 concurrent executions. Exceeding concurrent requests must be immediately rejected with a `BulkheadFullException`, yielding a 429 Too Many Requests status with a JSON error payload.

**Validation (Automated Test)**: Use asynchronous `WebTestClient` requests running in parallel. Assert that 2 reports execute concurrently while the 3rd request is instantly rejected with HTTP 429.

**Acceptance Scenarios**:
1. **Given** 2 active concurrent executions running on the `/report` stream, **When** a 3rd concurrent request is subscribed to, **Then** it is immediately rejected with a `BulkheadFullException` signal.

---

### Scenario 3 - Reactive Rate Limiting (Priority: P2)
Implement a weather forecast endpoint `/api/resilient/weather`. Configure a reactive `RateLimiter` allowing only 3 requests per 5 seconds. If a client exceeds this frequency, return HTTP 429 (Too Many Requests) with the header `Retry-After: 5`.

**Validation (Automated Test)**: Call `/api/resilient/weather` 4 times within a 1-second interval. Assert that the first 3 return 200 OK, while the 4th yields HTTP 429 with `RequestNotPermitted` mapping.

**Acceptance Scenarios**:
1. **Given** 3 completed requests in the current window, **When** a 4th request is initiated within 5 seconds, **Then** the pipeline throws `RequestNotPermitted` immediately.

---

### Scenario 4 - Reactive Time Limiters & Fallbacks (Priority: P2)
Create an analytics endpoint `/api/resilient/analytics`. Apply a `TimeLimiter` restricting request execution duration to 500ms. If the database retrieval takes longer, interrupt the stream and activate a fallback pipeline that returns cached static data.

**Validation (Automated Test)**: Call `/api/resilient/analytics` simulating a 1000ms delay. Assert the request completes in <600ms, returning the fallback analytics JSON body.

**Acceptance Scenarios**:
1. **Given** a stream delay exceeding 500ms, **When** the TimeLimiter trips, **Then** the subscriber receives a static cached fallback instead of a timeout error.

---

## Educational Requirements *(mandatory)*

### Concepts to Explain
- **EX-001**: Why standard thread-blocking resilience libraries (like Netflix Hystrix) fail in event-loop reactive streams, and why Resilience4j is optimized via `transformDeferred` bridges.
- **EX-002**: The state transition mechanics of a Circuit Breaker (CLOSED 🔄 OPEN 🔄 HALF_OPEN).
- **EX-003**: The difference between Semaphore-based Bulkheads (blocking concurrent permits) and Thread-Pool Bulkheads in reactive architectures.

### Technical Requirements
- **TR-001**: Project must depend on `io.github.resilience4j:resilience4j-reactor` and `spring-boot-starter-webflux`.
- **TR-002**: Validation MUST be fully automated using JUnit 5 and `WebTestClient`.
- **TR-003**: Custom error responses MUST conform strictly to JSON (no raw strings).
- **TR-004**: Custom Resilience4j configuration registries must be programmatically exposed as `@Bean` configurations to allow test control.
- **TR-005**: Provide collapsible Self-Assessment questions covering microservice isolation boundaries.

## Success Criteria *(measurable outcomes)*
- **SC-001**: The learner protects WebFlux REST APIs from downstream cascading failures using active circuit breaking and concurrent bulkheads.
- **SC-002**: 100% of integration tests pass natively using `mvn clean test` on the new module.
- **SC-003**: All scenarios include clear Traceable Implementation links.

## Assumptions
- Learner understands event-loops, publishers, and basic WebFlux routing mappings.
- Spring Boot 3.2+ and Java 21+ are used.
