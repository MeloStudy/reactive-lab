# Lab Specification: LAB-005: Essential Transformation Operators

**Feature Branch**: `005-essential-operators`
**Created**: 2026-05-03
**Status**: AUDITED
**Syllabus Section**: Level 1: Foundations (Project Reactor)

## Syllabus Alignment *(mandatory)*

- **Concept**: mastering synchronous and asynchronous transformation operators and their internal demand mechanics.
- **Prerequisites**: LAB-004: Subscriptions & Lifecycle Control.
- **Learning Objectives**:
  - LO-001: Implement 1-to-1 data transformations using `map`.
  - LO-002: Filter emissions based on predicates using `filter`.
  - LO-003: Understand asynchronous flattening with `flatMap` and the concept of **Concurrency** and **Prefetch**.
  - LO-004: Compare `flatMap` (interleaving) vs `concatMap` (sequential) behavior.
  - LO-005: Implement "latest-only" logic using `switchMap`.
  - LO-006: Optimize collection flattening with `flatMapIterable`.

## Interactive Scenarios & Validation *(mandatory)*

### Scenario 1 - The Async User Enrichment (Priority: P1)
Given a stream of User IDs, the learner must fetch full User details from an asynchronous "service" (mocked) using `flatMap`.
**Validation**: Verify that details are emitted, potentially out of order (proving interleaving).

### Scenario 2 - The Ordered Command Runner (Priority: P1)
Given a stream of sequential tasks (e.g., File Uploads), the learner must ensure they are executed strictly in order using `concatMap`.
**Validation**: Verify that the order of emission matches the order of input IDs perfectly.

### Scenario 3 - The Search Debouncer (Priority: P1)
Given a rapid stream of keyboard inputs, the learner must use `switchMap` to cancel pending search requests when a new key is pressed.
**Validation**: Use `StepVerifier` with virtual time to verify that only the results for the LAST input are emitted.

### Scenario 4 - The Prefetch Observer (Priority: P2)
The learner will use `flatMap` with a low `maxConcurrency` and `prefetch` value to observe (via `doOnRequest`) how Project Reactor manages upstream demand.
**Validation**: Assert the number of request signals sent to the source.

---

## Educational Requirements *(mandatory)*

### Concepts to Explain
- **EX-001**: **Flattening**: Why we need `flatMap` instead of `map` when dealing with nested `Publishers`.
- **EX-002**: **Interleaving vs Sequencing**: The performance vs order trade-off between `flatMap` and `concatMap`.
- **EX-003**: **Prefetch & Concurrency**: How operators manage internal buffers and demand.
- **EX-004**: **Resource Cleanup**: How `switchMap` automatically cancels the "inner" subscription.

### Technical Requirements
- **TR-001**: Use Java 21 and Project Reactor.
- **TR-002**: Use `StepVerifier` for all validations.
- **TR-003**: Use `PublisherProbe` to verify cancellation signals in `switchMap`.
- **TR-004**: README MUST include an **Interactive Self-Assessment** (Constitution v0.1.2).
- **TR-005**: Provide "Command Dissection" for `flatMap` parameters.

## Success Criteria
- **SC-001**: Correct choice of operator based on ordering requirements.
- **SC-002**: Successful implementation of a debounced search simulation.
- **SC-003**: All validation tests pass.
