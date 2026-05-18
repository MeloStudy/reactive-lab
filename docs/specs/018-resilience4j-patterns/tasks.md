# Tasks: LAB-018: Advanced Resilience Patterns (Resilience4j) [READY]

**Input**: Design documents from `/specs/018-resilience4j-patterns/`
**Prerequisites**: plan.md (required), spec.md (required).
**Constitution Version**: v0.2.8

**Validation Goal**: This project follows strict "TDD for Learning" (Test-Driven Learning). The automated tests (JUnit 5 + WebTestClient) must thoroughly verify all resilience mappings, state transitions, concurrent bulkheads, rate limits, and time-bound fallbacks.

## Phase 1: Monorepo Setup & Base Scaffolding
- [ ] T001 Scaffold lab directory structure `labs/018-resilience4j-patterns/`.
- [ ] T002 Register the new Java module `<module>labs/018-resilience4j-patterns</module>` in the root `pom.xml`.
- [ ] T003 Configure `labs/018-resilience4j-patterns/pom.xml` inheriting the parent POM with WebFlux and Resilience4j Reactor starters.

## Phase 2: Core Infrastructure & Scenario Implementation (TDD)
- [ ] T004 Implement `ResilienceConfig` exposing programmatic `CircuitBreakerRegistry`, `BulkheadRegistry`, `RateLimiterRegistry`, and `TimeLimiterRegistry` beans.
- [ ] T005 Implement Scenario 1: Setup `Order` model, Order controller with `CircuitBreakerOperator`, mock default fallback, and write `WebTestClient` verification simulating state changes (CLOSED ➡️ OPEN ➡️ HALF_OPEN).
- [ ] T006 Implement Scenario 2: Setup `Report` model, Report controller with Semaphore-based `BulkheadOperator`, HTTP 429 BulkheadFull mapping, and parallel async integration verification.
- [ ] T007 Implement Scenario 3: Setup `/weather` endpoint with `RateLimiterOperator`, mapping `RequestNotPermitted` to HTTP 429, and rapid-firing validation.
- [ ] T008 Implement Scenario 4: Setup `/analytics` endpoint with `TimeLimiterOperator`, mapping slow service delays to cached static JSON fallbacks, and verification.
- [ ] T009 Add exhaustive comments above all test methods explaining exactly what every pipeline validation asserts.

## Phase 3: Educational Content & Clean Up Verifications
- [ ] T010 Write the theoretical `CONCEPT.md` detailing non-blocking reactive thread-hopping, Hystrix ThreadLocals, and Circuit Breaker state machines.
- [ ] T011 Write the step-by-step native `README.md` guide with direct traceable implementation links.
- [ ] T012 Inject **Command Dissection** blocks into `README.md` for `transformDeferred` and builder registries.
- [ ] T013 Implement "Self-Assessment" knowledge check using collapsible details blocks.

## Phase 4: Verification & Certification
- [ ] T014 Execute `mvn clean test -pl labs/018-resilience4j-patterns` to verify all integration tests build and pass successfully.
- [ ] T015 Verify compliance with the Constitution (package structure, access modifiers, no shadowed versions).
