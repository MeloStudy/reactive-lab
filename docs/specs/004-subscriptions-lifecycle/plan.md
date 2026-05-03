# Implementation Plan: LAB-004: Subscriptions & Lifecycle Control

**Branch**: `004-subscriptions-lifecycle` | **Date**: 2026-05-02
**Input**: Specification from `/docs/specs/004-subscriptions-lifecycle/spec.md`

## Summary

The goal of this lab is to move beyond "passive" subscriptions and learn how to actively manage the lifecycle of a stream. This includes manual cancellation (critical for resource management) and implementing custom subscribers for fine-grained demand control.

## Phase 1: Java Project Setup
1. **Scaffold**: Create `labs/004-subscriptions-lifecycle` with a standard Reactor/JUnit POM.
2. **Package**: `com.reactivelab.lifecycle`.

## Phase 2: Scenario 1 - Manual Cancellation
1. **Instructional Path**: Create an infinite `Flux.interval` and show how to use `Disposable.dispose()` to stop it.
2. **Testing**: Use `StepVerifier.withVirtualTime` to simulate time passage and verify that no more items are emitted after cancellation.

## Phase 3: Scenario 2 - Grouped Cancellation (Composite)
1. **Instructional Path**: Create multiple intervals and add them to a `CompositeDisposable`. Show that one `dispose()` stops all.
2. **Testing**: Verify all streams are disposed.

## Phase 4: Scenario 3 - Custom Demand (BaseSubscriber)
1. **Instructional Path**: Implement a `SmartSubscriber` that extends `BaseSubscriber`. It should request 1 item, process it, then request the next one (manual loop).
2. **Testing**: Verify that the subscriber only receives what it requests.

## Phase 5: Scenario 4 - Automatic Lifecycle (take)
1. **Instructional Path**: Compare manual `dispose()` with `.take(5)`. Show that `take` handles the cancellation signal for us.
2. **Testing**: Use `doOnCancel` to verify that `take` emits a cancel signal.

## Phase 6: Scenario 5 - Lifecycle Hooks
1. **Instructional Path**: Use `doOnSubscribe`, `doOnNext`, `doOnComplete`, `doFinally` to track a pipeline's lifecycle.
2. **Testing**: Use `AtomicBoolean` flags to verify that all hooks were executed in the expected order.


## Phase 5: Documentation
1. **CONCEPT.md**: Explain the internal state machine of a Subscription.
2. **README.md**: Native execution guide for each scenario.

## Constitution Compliance Check
- [ ] No `.sh` wrapper scripts.
- [ ] Code comments explicitly describe what every test line validates.
- [ ] Maven native commands used throughout.
- [ ] Backpressure Scenarios: Scenario 2 covers manual demand.

## Open Questions
- Should we introduce `Sinks` here? (Recommendation: No, keep it for LAB-007).
- Is virtual time too advanced for Level 1? (Decision: Necessary for testing intervals without long wait times).
