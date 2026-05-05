# Tasks: LAB-013: Error Handling & Resilience in WebFlux [AUDITED]

**Input**: Design documents from `docs/specs/013-error-handling-resilience/`
**Prerequisites**: plan.md, spec.md.

**Validation Goal**: Create a series of failing scenarios that are "tamed" by different reactive error handling strategies, validated via `WebTestClient`.

## Phase 1: Infrastructure & Project Setup
- [x] T001 Create `labs/013-error-handling-resilience` directory structure.
- [x] T002 Update root `pom.xml` module registration.
- [x] T003 Configure `pom.xml` dependencies for WebFlux and Testing.

## Phase 2: Scenario Implementation (TDD)
- [x] T004 Implement Scenario 1: `onErrorReturn` fallback test and controller logic.
- [x] T005 Implement Scenario 2: `@ExceptionHandler` test and controller logic.
- [x] T006 Implement Scenario 3: `GlobalErrorWebExceptionHandler` with `ProblemDetail`.
- [x] T007 Implement Scenario 4: Context propagation (`Correlation-ID`) in error responses.
- [x] T008 Implement Scenario 5: `timeout()` validation test.
- [x] T009 Implement Scenario 6: `retryWhen` logic with MockWebServer.
- [x] T010 Implement Scenario 7: `HandlerFilterFunction` error handling validation.

## Phase 3: Documentation & Educational Content
- [x] T011 Create `CONCEPT.md` detailing the reactive error signal propagation.
- [x] T012 Create `README.md` with walkthroughs and Command Dissections.
- [x] T013 Implement "Self-Assessment" knowledge check in `README.md`.

## Phase 4: Final Certification
- [x] T014 Verify all tests pass with `mvn test`.
- [x] T015 Verify compliance with the Constitution.
- [x] T016 Update `docs/syllabus.md` status to `AUDITED`.
