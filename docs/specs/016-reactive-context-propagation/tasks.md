# Tasks: LAB-016: Reactive Context & Tracing Propagation

**Input**: Design documents from `/specs/016-reactive-context-propagation/`
**Prerequisites**: plan.md (required), spec.md (required).

**Validation Goal**: Ensure that learners can reliably propagate and retrieve metadata from the Reactor Context across different threading boundaries.

## Phase 1: Monorepo Setup & Infrastructure
- [x] T001 Create Maven module `labs/016-reactive-context-propagation`.
- [x] T002 Update root `pom.xml` with the new module.
- [x] T003 Configure Logback with a custom pattern to display MDC variables (e.g., `%X{correlationId}`).

## Phase 2: Validation Framework Implementation (TDD)
- [x] T004 Create `ContextPropagationTest.java`.
- [x] T005 Implement test case for Scenario 1: MDC propagation across `Schedulers`.
- [x] T006 Implement test case for Scenario 2: Security User retrieval from Context.
- [x] T007 Add detailed comments to tests explaining the "Signal vs Context" relationship.

## Phase 3: Educational Content & Hands-On Environment
- [x] T008 Write `CONCEPT.md` focusing on the immutability of `Context` and why `ThreadLocal` is dangerous in Netty/WebFlux.
- [x] T009 Write `README.md` with the "Correlation ID" challenge.
- [x] T010 Add **Command Dissection** for `contextWrite` and `deferContextual`.
- [x] T011 Implement the **Interactive Self-Assessment** section in `README.md`.

## Phase 4: Idempotency & Finalization
- [x] T012 Verify `mvn clean test` passes for the new module.
- [x] T013 Update `syllabus.md` status for LAB-016 to `AUDITED`.
- [x] T014 Perform end-to-end "Learner Journey" walkthrough.
