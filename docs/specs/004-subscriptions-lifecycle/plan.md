# Implementation Plan: LAB-004: Subscriptions, Lifecycle & Side Effects

**Branch**: `004-subscriptions-lifecycle` | **Date**: 2026-05-02
**Input**: Specification from `/docs/specs/004-subscriptions-lifecycle/spec.md`

## Summary

The goal of this lab is to move beyond "passive" subscriptions and learn how to actively manage the lifecycle of a stream. This includes manual cancellation (critical for resource management), implementing custom subscribers for fine-grained demand control, and observing stream signals using "Peekers" (Side Effects).

## Phase 1: Java Project Setup
1. **Scaffold**: Create `labs/004-subscriptions-lifecycle` with a standard Reactor/JUnit POM.
2. **Package**: `com.reactivelab.lifecycle`.

## Phase 2: Scenario 1 - Manual Cancellation
1. **Instructional Path**: Create an infinite `Flux.interval` and show how to use `Disposable.dispose()` to stop it.
2. **Testing**: Use `StepVerifier.withVirtualTime` to simulate time passage and verify that no more items are emitted after cancellation.

## Phase 3: Scenario 2 - Grouped Cancellation (Composite)
1. **Instructional Path**: Create multiple intervals and add them to a `Disposables.composite()`. Show that one `dispose()` stops all.
2. **Testing**: Verify all streams are disposed.

## Phase 4: Scenario 3 - Custom Demand (BaseSubscriber)
1. **Instructional Path**: Implement a `SmartSubscriber` that extends `BaseSubscriber`. It should request 1 item, process it, then request the next one (manual loop).
2. **Testing**: Verify that the subscriber only receives what it requests.

## Phase 5: Scenario 4 - Automatic Lifecycle (take)
1. **Instructional Path**: Compare manual `dispose()` with `.take(5)`. Show that `take` handles the cancellation signal for us.
2. **Testing**: Use `doOnCancel` to verify that `take` emits a cancel signal.

## Phase 6: Scenario 5 - Signal Peekers (Side Effects)
1. **Instructional Path**: Use `doOnSubscribe`, `doOnNext`, `doOnComplete`, `doOnError`, and `doFinally` to track a pipeline's lifecycle without modifying data.
2. **Testing**: Use `AtomicInteger` counters and `AtomicBoolean` flags to verify that all hooks were executed in the expected order and frequency.

## Phase 7: Documentation
1. **CONCEPT.md**: Explain the internal state machine of a Subscription and the "Observation vs Transformation" pattern.
2. **README.md**: Native execution guide for each scenario, including the Signal Peeker demo.

## Phase 8: Refinement (Constitution Audit)
1. **Standards**: Ensure non-public tests and SLF4J logging.
2. **Theoretical Depth**: Deep dive into the Subscription state machine in `CONCEPT.md`.

## Constitution Compliance Check
- [x] No `.sh` wrapper scripts.
- [x] Code comments explicitly describe what every test line validates.
- [x] Maven native commands used throughout.
- [x] Backpressure Scenarios: Scenario 3 covers manual demand.

## Open Questions
- Is virtual time too advanced for Level 1? (Decision: Necessary for testing intervals without long wait times).
