# Implementation Plan: LAB-012: WebClient: Orchestrating Downstream Services [AUDITED]

**Branch**: `012-webclient-orchestration` | **Date**: 2026-05-04
**Input**: Specification from `docs/specs/012-webclient-orchestration/spec.md`

## Summary

The learner will implement a reactive gateway service that orchestrates multiple external microservices using Spring's `WebClient`. The focus is on mastering non-blocking HTTP communication, efficient resource management (connections/memory), and complex stream composition (parallel vs. sequential).

## Phase 1: Monorepo Infrastructure (Base Setup)
1. **Scaffold**: Create `labs/012-webclient-orchestration/` with standard Maven structure.
2. **Workspace Registration**: Add the lab to the root `pom.xml`.
3. **External Mocks**: Configure `MockWebServer` in the test suite to simulate downstream services without external dependencies.

## Phase 2: Scenario 1 & 2 - Foundations & Parallelism
1. **Instructional Path**: 
   - Configuring `WebClient` as a shared Bean.
   - Using `retrieve()` to fetch JSON.
   - Using `Flux.zip` to combine multiple service results into a single payload.
2. **Validation**: JUnit 5 + `StepVerifier` + `MockWebServer` asserting that calls happen concurrently.

## Phase 3: Scenario 3 & 4 - Dependencies & Resilience
1. **Instructional Path**:
   - Using `flatMap` for dependent calls (Fetch A -> Fetch B).
   - Error handling via `onStatus` for specific HTTP codes.
   - Global fallbacks and retries using `retryWhen`.
2. **Validation**: Test cases that induce failures in the mock server and verify the client's retry logic and fallback responses.

## Phase 4: Full Documentation & Dissection
1. **CONCEPT.md**: Explain the underlying architecture of `WebClient` (based on Netty), the importance of body consumption in `exchange()`, and the threading model.
2. **README.md**: Educational walkthrough with "Command Dissection" for `WebClient.builder()`, `retrieve()`, and `onStatus()`.

## Phase 5: BlockHound & Manual Control
1. **Instructional Path**:
   - Integrating BlockHound into JUnit 5.
   - Using `exchangeToMono` for precise response handling.
   - Managing resource cleanup (connection release).
2. **Validation**: Tests that attempt to block (e.g., `Thread.sleep`) and verify that BlockHound throws an exception.


## Constitution Compliance Check
- [ ] No `.sh` wrapper scripts.
- [ ] Code comments explicitly describe what every test line validates.
- [ ] Language used across all text is explicitly English.
- [ ] **Dependency Governance**: Inherits from root parent POM, no local versions.

## Open Questions
- **Decision on Mocks**: We will use `MockWebServer` (OkHttp) integrated into unit tests to ensure high speed and focus on `WebClient` orchestration logic.
- **Decision on SSL**: SSL/TLS configuration is deferred to a future "Security & Production Readiness" lab.
