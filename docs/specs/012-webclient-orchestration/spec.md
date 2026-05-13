# Lab Specification: LAB-012: WebClient: Orchestrating Downstream Services [AUDITED]

**Feature Branch**: `012-webclient-orchestration`
**Created**: 2026-05-04
**Status**: Audited
**Syllabus Section**: Level 3: Enterprise Reactive Services (Spring WebFlux)

## Syllabus Alignment *(mandatory)*

- **Concept**: Non-blocking service-to-service communication using `WebClient`.
- **Prerequisites**: LAB-011 (Reactive Web with Spring WebFlux), LAB-006 (Combining Operators).
- **Learning Objectives**:
  - LO-001: Configure and instantiate `WebClient` as a Bean.
  - LO-002: Execute GET/POST requests using the `retrieve()` API.
  - LO-003: Distinguish between `retrieve()` and `exchangeToMono()`/`exchangeToFlux()` (managing memory and body consumption).
  - LO-004: Orchestrate parallel calls using `zip` and sequential dependencies using `flatMap`.
  - LO-005: Implement robust error handling with `onStatus()` and `onErrorResume()`.
  - LO-006: Apply resilience patterns: `timeout()` and `retry()`.

## Interactive Scenarios & Validation *(mandatory)*

### Scenario 1 - The Base Fetcher (P1)
Configure a `WebClient` to call a mock "User Service". Fetch a user profile by ID and convert the JSON response into a `Mono<User>`.

**Validation (Automated Test)**: Use `StepVerifier` and a MockWebServer to verify that the client correctly maps the JSON fields and handles the 200 OK signal.

---

### Scenario 2 - Parallel Orchestration (P1)
Call the "User Service" and "Orders Service" simultaneously for a specific ID. Combine both results into a `UserDashboard` object.

**Validation (Automated Test)**: Verify that the total execution time is approximately the time of the slowest service, not the sum of both (demonstrating non-blocking parallelism).

---

### Scenario 3 - The Preference Chain (P2)
Fetch a User profile first, then use a field from that profile (e.g., `preferenceId`) to make a second call to a "Preferences Service".

**Validation (Automated Test)**: Ensure the calls happen in sequence and that the final result contains data from both services, validated via `StepVerifier`.

---

### Scenario 4 - Failover & Resilience (P2)
Call an "Inventory Service" that is known to be flaky (simulated with 503 errors and 5-second delays). Implement a 2-second timeout and a retry strategy with 3 attempts.

**Validation (Automated Test)**: Verify that the client retries exactly 3 times before failing or returning a fallback value, and that the timeout triggers as expected.

---

### Scenario 5 - Consuming the Stream (P3)
Connect to a remote SSE (Server-Sent Events) endpoint that streams "Global Events" and filter them based on a specific type before returning the flux to the caller.

**Validation (Automated Test)**: Verify that the `WebClient` can handle long-running streams without buffer overflows.

---

### Scenario 6 - Advanced Body Control (P2)
Use `exchangeToMono` to fetch a resource where you need to check specific headers before decided how to consume the body. This scenario emphasizes the responsibility of manual body consumption to prevent memory leaks.

**Validation (Automated Test)**: Verify that the response body is correctly consumed or discarded based on header values.


## Educational Requirements *(mandatory)*

### Concepts to Explain
- **EX-001**: Why `RestTemplate` is deprecated in favor of `WebClient`.
- **EX-002**: The memory safety of `retrieve()` vs the responsibility of `exchange()`.
- **EX-003**: Threading in WebClient: How the Event Loop handles I/O.

### Technical Requirements
- **TR-001**: Use `spring-boot-starter-webflux`.
- **TR-002**: Use `okhttp3.mockwebserver` for testing external calls.
- **TR-003**: No blocking calls allowed. This MUST be enforced using **BlockHound** in the test suite.

## Success Criteria *(measurable outcomes)*
- **SC-001**: Learner successfully orchestrates 3 parallel calls and combines them in under the time of the slowest call.
- **SC-002**: Learner identifies and fixes a memory leak caused by not consuming a response body in an `exchange` call.

## Assumptions
- Spring Boot 3.2+ and Java 21+ are used.
- Basic knowledge of `zip` and `flatMap` from Level 1 labs.
