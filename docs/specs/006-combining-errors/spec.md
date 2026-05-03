# Lab Specification: LAB-006: Combining & Basic Error Handling

**Feature Branch**: `006-combining-errors`
**Created**: 2026-05-03
**Status**: AUDITED
**Syllabus Section**: Level 1: Foundations (Project Reactor)

## Syllabus Alignment *(mandatory)*

- **Concept**: Orchestrating multiple streams and implementing basic resilience patterns.
- **Prerequisites**: LAB-005: Essential Transformation Operators.
- **Learning Objectives**:
  - LO-001: Combine streams eagerly with `merge` and understand interleaving.
  - LO-002: Combine streams sequentially with `concat`.
  - LO-003: Pair elements from different sources with `zip`.
  - LO-004: Handle errors with side-effects using `doOnError`.
  - LO-005: Implement recovery fallbacks using `onErrorReturn` and `onErrorResume`.
  - LO-006: Translate exceptions using `onErrorMap`.
  - LO-007: Apply basic transient error recovery with `retry`.

## Interactive Scenarios & Validation *(mandatory)*

### Scenario 1 - The Dashboard Aggregator (Priority: P1)
Combine a User profile (`Mono`) with their recent Activity (`Flux`) and Friends count (`Mono`).
- Use `zip` to combine Profile + Friends into a Header.
- Use `merge` to display Activity alongside other notifications.
**Validation**: Verify consolidated object contains all data.

### Scenario 2 - The Resilient Service (Priority: P1)
A service that calls an unstable API.
- If it fails, log the error with `doOnError`.
- Map technical exceptions (e.g., `TimeoutException`) to business exceptions with `onErrorMap`.
- Provide a static default object if it fails completely using `onErrorReturn`.
**Validation**: Verify that the stream never terminates with an error and returns the default.

### Scenario 3 - The Failover Strategy (Priority: P1)
Attempt to fetch data from "Source A". If it fails, switch to "Source B" using `onErrorResume`.
**Validation**: Verify that data from Source B is emitted when Source A throws an error.

### Scenario 4 - The Flaky Network (Priority: P2)
A stream that emits an error 50% of the time. Use `retry(3)` to stabilize it.
**Validation**: Assert that the stream eventually completes successfully after a few attempts.

---

## Educational Requirements *(mandatory)*

### Concepts to Explain
- **EX-001**: **Eager vs Lazy Combination**: `merge` (interleaved) vs `concat` (waiting).
- **EX-002**: **Zip Cardinality**: Why `zip` waits for all sources and completes when the shortest source finishes.
- **EX-003**: **The Error Channel**: How errors move through the pipeline and how they terminate subscriptions.
- **EX-004**: **Recovery vs Side-Effect**: Difference between `doOnError` (peek) and `onErrorResume` (swallow and replace).

### Technical Requirements
- **TR-001**: Use Java 21 and Project Reactor.
- **TR-002**: Use `StepVerifier` for all validations.
- **TR-003**: README MUST include an **Interactive Self-Assessment** (Constitution v0.1.2).
- **TR-004**: Document the "Error Terminal Rule": A stream is dead after an unhandled error.

## Success Criteria
- **SC-001**: Successful orchestration of 3+ sources into a single object.
- **SC-002**: Implementation of a multi-tier fallback strategy.
- **SC-003**: All validation tests pass.
