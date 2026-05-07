# Lab Specification: LAB-010: Testing & Debugging Matrix

**Feature Branch**: `010-testing-debugging-matrix`
**Created**: 2026-05-04
**Status**: AUDITED
**Syllabus Section**: Level 2: The Core Spec & Advanced Control

## Syllabus Alignment *(mandatory)*

- **Concept**: Advanced verification, time-warping, and debugging non-blocking pipelines.
- **Prerequisites**: LAB-007 (Threading), LAB-009 (Backpressure).
- **Learning Objectives**:
  - LO-001: Master `StepVerifier.withVirtualTime` for time-sensitive streams.
  - LO-002: Use `PublisherProbe` to verify branching logic (conditional streams).
  - LO-003: Understand and use **Reactor Context** for cross-cutting concerns (Trace IDs).
  - LO-004: Debug asynchronous stack traces using `Hooks.onOperatorDebug()` and `checkpoint()`.
  - LO-005: Integrate **BlockHound** to enforce non-blocking execution.

## Interactive Scenarios & Validation *(mandatory)*

### Scenario 1 - The Time Traveler (Priority: P1)
Verify a stream that emits data once per day. Test 365 days of emissions in milliseconds.
**Validation**: `StepVerifier` with `thenAwait(Duration.ofDays(365))` and `expectNextCount(365)`.

### Scenario 2 - The Branch Validator (Priority: P1)
Use `switchIfEmpty` and verify that the fallback branch is actually subscribed to using `PublisherProbe`.
**Validation**: `probe.assertWasSubscribed()`.

### Scenario 3 - The Trace Hunter (Priority: P1)
Propagate a `correlationId` using `Context`. Perform a `publishOn` (thread hop) and verify the ID is still accessible.
**Validation**: Assert context contains the expected key-value pair after the thread switch.

### Scenario 4 - The BlockHound Sentry (Priority: P1)
Simulate an accidental blocking call (`Thread.sleep`) in a reactive stream. Ensure BlockHound throws a `BlockingOperationError`.
**Validation**: Verify that the test fails with the specific BlockHound exception.

### Scenario 5 - The Checkpoint Trace (Priority: P2)
Use `checkpoint("my-custom-step")` to label a pipeline segment and identify where an error originated in the logs.
**Validation**: Find the custom label in the error stack trace.

---

## Educational Requirements *(mandatory)*

### Concepts to Explain
- **EX-001**: **Assembly vs Execution**: Why the stack trace points to where the Flux was *created*, not where it *failed*.
- **EX-002**: **The Scheduler Mocking**: How `VirtualTimeScheduler` hijacks the clock.
- **EX-003**: **Contextual State**: Why Context is a "down-to-up" signal (it travels against the data flow).
- **EX-004**: **BlockHound Mechanics**: How it uses bytecode instrumentation to detect forbidden calls.

### Technical Requirements
- **TR-001**: Use Java 21 and Project Reactor.
- **TR-002**: Use `StepVerifier` for all validations.
- **TR-003**: Include BlockHound dependency in `pom.xml`.

## Success Criteria
- SC-001: Successful verification of long-duration streams via Virtual Time.
- SC-002: Reliable detection of blocking code in reactive threads.
- SC-003: Demonstration of state propagation via Context.
- SC-004: All validation tests pass.
