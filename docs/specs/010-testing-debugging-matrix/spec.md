# Lab Specification: LAB-010: Testing & Debugging Matrix [AUDITED]

**Feature Branch**: `010-testing-debugging-matrix`
**Created**: 2026-05-04
**Status**: Audited
**Syllabus Section**: Level 2: The Core Spec & Advanced Control

## Syllabus Alignment *(mandatory)*

- **Concept**: Advanced Testing (`StepVerifier`, `PublisherProbe`) and Debugging (`Hooks`, `Context`).
- **Prerequisites**: LAB-009 (Backpressure), LAB-007 (Threading).
- **Learning Objectives**:
  - LO-001: Use `StepVerifier.withVirtualTime` to test long-running streams instantly.
  - LO-002: Use `PublisherProbe` to verify that a fallback or side-effect branch was executed.
  - LO-003: Resolve "Missing Assembly" stack traces using `Hooks.onOperatorDebug()`.
  - LO-004: Propagate metadata through a pipeline using `Context`.
  - LO-005: Use `BlockHound` to automatically detect blocking calls on the event loop threads.

## Interactive Scenarios & Validation *(mandatory)*

### Scenario 1 - The Time Traveler (P1)

Test a stream that emits elements every hour. The learner must use `VirtualTimeScheduler` to verify the first 24 hours of data in milliseconds.

**Validation (Automated Test)**: `StepVerifier` using `thenAwait` and `expectNextCount` to verify the full day of data.

---

### Scenario 2 - The Silent Branch (P2)

In a complex `switchIfEmpty` or `onErrorResume` scenario, verify that the fallback branch was actually "subscribed" to, even if it produces no data.

**Validation (Automated Test)**: Use `PublisherProbe` to assert `assertWasSubscribed()`.

---

### Scenario 3 - The Trace Hunter (P2)

A pipeline fails with a cryptic `NullPointerException` deep in a `map` operator. The learner must enable debugging hooks to identify the exact line of assembly.

**Validation (Manual/Guided)**: README instructions to enable hooks and analyze the "Assembly Stacktrace".

### Scenario 4 - The Forbidden Block (P3)

A developer accidentally includes a `Thread.sleep()` or a blocking I/O call inside a `flatMap` running on a Schedulers thread pool. The learner must configure BlockHound to detect and prevent this.

**Validation (Automated Test)**: A test that triggers a blocking call and expects a `BlockingOperationError` when BlockHound is installed.

---

## Educational Requirements *(mandatory)*

### Concepts to Explain

- **EX-001**: Why standard stack traces are useless in reactive programming (Assembly vs Execution time).
- **EX-002**: Reactor Context: The non-blocking equivalent of `ThreadLocal`.
- **EX-003**: Testing Time: Why `Thread.sleep` is an anti-pattern in reactive tests.

### Technical Requirements

- **TR-001**: Lab MUST include automated validation tests (Java/JUnit).
- **TR-002**: Reactive signals MUST be explicitly validated.
- **TR-003**: Theoretical context MUST be provided in `CONCEPT.md`.

## Success Criteria *(measurable outcomes)*

- **SC-001**: Learner successfully tests a 1-year duration stream in under 1 second.
- **SC-002**: Learner identifies a bug using `checkpoint()` or `Hooks`.

## Assumptions

- Java 21+ and Maven are installed.
