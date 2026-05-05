# Lab Specification: LAB-013: Error Handling & Resilience in WebFlux [AUDITED]

**Feature Branch**: `013-error-handling-resilience`
**Created**: 2026-05-04
**Status**: Audited
**Syllabus Section**: Level 3: Enterprise Reactive Services (Spring WebFlux)

## Syllabus Alignment *(mandatory)*

- **Concept**: Implementing robust error handling and resilience patterns at the web layer.
- **Prerequisites**: LAB-011 (WebFlux Basics), LAB-012 (WebClient), LAB-006 (Error Operators).
- **Learning Objectives**:
  - LO-001: Implement pipeline-level recovery using `onErrorResume` and `onErrorReturn`.
  - LO-002: Handle exceptions via `@ExceptionHandler` in annotated controllers.
  - LO-003: Build a `GlobalErrorWebExceptionHandler` using `ProblemDetail` (RFC 7807).
  - LO-004: Propagate and include `Correlation-ID` from Reactor Context in error responses.
  - LO-005: Apply server-side timeouts using the `timeout()` operator.
  - LO-006: Orchestrate resilient retries using `retryWhen`.
  - LO-007: Handle errors in functional `HandlerFilterFunction`.
  - LO-008: Customize `DefaultErrorAttributes` for consistent API error contracts.

## Interactive Scenarios & Validation *(mandatory)*

### Scenario 1 - Local Fallback (P1)
Create an endpoint `/items/{id}` that calls a service. If the service fails, use `onErrorReturn` to return a "Default Item" with a 200 OK status.

**Validation (Automated Test)**: Use `WebTestClient` to assert that even when the service throws an error, the endpoint returns the default item.

---

### Scenario 2 - Controller Exception Handler (P1)
Implement a custom `ProductNotFoundException`. Use `@ExceptionHandler` in the controller to catch it and return a 404 Not Found status with a custom error message.

**Validation (Automated Test)**: Verify that throwing the exception results in a 404 status and the expected JSON body.

---

### Scenario 3 - RFC 7807 & Problem Details (P1)
Implement a global handler that leverages Spring's `ProblemDetail` support. All errors must return a `Content-Type: application/problem+json` response.

**Validation (Automated Test)**: Assert the `Content-Type` and the presence of standard fields like `type`, `title`, `status`, and `detail`.

---

### Scenario 4 - Context-Aware Errors (P2)
Extract a `trace-id` or `correlation-id` from the **Reactor Context** and include it as a custom field in the error response.

**Validation (Automated Test)**: Use `contextWrite` in the test to provide a trace-ID and verify it appears in the final error JSON.

---

### Scenario 5 - Execution Timeout (P2)
Simulate a slow database call (using `delayElement`). Apply a `.timeout(Duration.ofSeconds(1))` in the controller.

**Validation (Automated Test)**: Verify that the endpoint returns a 408 Request Timeout after 1 second.

---

### Scenario 6 - Intelligent Retries (P2)
Implement an endpoint that calls an external service via `WebClient`. Use `retryWhen` with exponential backoff (starting at 100ms, 3 attempts) specifically for `ServiceUnavailableException`.

**Validation (Automated Test)**: Use `MockWebServer` to fail twice and succeed on the third attempt.

---

### Scenario 7 - Filter-Level Error Handling (P3)
Implement a `HandlerFilterFunction` that validates a header. If missing, throw an exception and ensure it is caught correctly by the global handler.

**Validation (Automated Test)**: Call the endpoint without the header and verify the global error response.

## Educational Requirements *(mandatory)*

### Concepts to Explain
- **EX-001**: Why traditional `try-catch` doesn't work in reactive pipelines.
- **EX-002**: The precedence of error handlers: Local -> Controller -> Global.
- **EX-003**: The difference between `onErrorResume` (returning a new stream) and `onErrorReturn` (returning a value).

### Technical Requirements
- **TR-001**: Use `spring-boot-starter-webflux`.
- **TR-002**: Validation MUST use `WebTestClient`.
- **TR-003**: Custom error responses MUST be strictly JSON.

## Success Criteria *(measurable outcomes)*
- **SC-001**: Learner successfully unifies all application errors into a single JSON format.
- **SC-002**: Learner differentiates between a "Business Error" (handled locally) and a "System Error" (handled globally).

## Assumptions
- Spring Boot 3.2+ and Java 21+ are used.
