# Lab Specification: LAB-008: Programmatic Stream Generation

**Feature Branch**: `008-programmatic-streams`
**Created**: 2026-05-03
**Status**: AUDITED
**Syllabus Section**: Level 2: The Core Spec & Advanced Control

## Syllabus Alignment *(mandatory)*

- **Concept**: Manual control of stream emission patterns and bridging imperative-to-reactive boundaries.
- **Prerequisites**: LAB-007: Threading Models & Schedulers.
- **Learning Objectives**:
  - LO-001: Generate synchronous, stateful sequences with `Flux.generate`.
  - LO-002: Bridge push-based callback APIs with `Flux.create`.
  - LO-003: Understand the push vs. pull emission models.
  - LO-004: Implement standalone programmatic publishers with `Sinks.Many` and `Sinks.One`.
  - LO-005: Manage memory safety in push bridges using basic `OverflowStrategy`.

## Interactive Scenarios & Validation *(mandatory)*

### Scenario 1 - The Stateful Fibonacci (Priority: P1)
Implement the Fibonacci sequence using `Flux.generate`.
- Use the state-handling overload of `generate`.
- Ensure the state is correctly updated between emissions.
**Validation**: Verify the first 10 numbers of the sequence.

### Scenario 2 - The Legacy Chat Bridge (Priority: P1)
Wrap a mock `ChatListener` that receives messages asynchronously.
- Use `Flux.create` to register the listener on subscription.
- Use `sink.onDispose()` to unregister the listener when cancelled.
**Validation**: Verify that messages pushed to the mock listener appear in the Flux.

### Scenario 3 - The Overflow Teaser (Priority: P1)
Simulate a fast producer in `Flux.create` and a slow consumer.
- Apply `OverflowStrategy.DROP` or `LATEST`.
- Verify that the consumer only receives a subset of items without crashing.
**Validation**: Assert that the total count of received items is less than emitted items.

### Scenario 4 - The Global Notification Bus (Priority: P1)
Implement a singleton `NotificationBus` using `Sinks.Many.multicast().onBackpressureBuffer()`.
- Multiple subscribers should receive the same notification.
**Validation**: Emit one message and verify multiple `StepVerifier` instances receive it.

---

## Educational Requirements *(mandatory)*

### Concepts to Explain
- **EX-001**: **Generator Contract**: Why `Flux.generate` can only emit ONE item per iteration.
- **EX-002**: **Bridging Risks**: Why `Flux.create` is dangerous if the source is faster than the consumer.
- **EX-003**: **Sinks as the Modern Way**: Why `Sinks` are preferred over the older `Processor` API.
- **EX-004**: **Resource Management**: The importance of `onDispose` and `onCancel` in bridges.

### Technical Requirements
- **TR-001**: Use Java 21 and Project Reactor.
- **TR-002**: Use `StepVerifier` for all validations.
- **TR-003**: README MUST include an **Interactive Self-Assessment** (Constitution v0.1.2).

## Success Criteria
- **SC-001**: Correct implementation of a stateful mathematical sequence.
- **SC-002**: Successful bridging of a callback-based API with proper cleanup.
- **SC-003**: Demonstration of overflow handling (LATEST/DROP).
- **SC-004**: All validation tests pass.
