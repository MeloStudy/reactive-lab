# Lab Specification: LAB-004: Subscriptions & Lifecycle Control

**Feature Branch**: `004-subscriptions-lifecycle`
**Created**: 2026-05-02
**Status**: AUDITED
**Syllabus Section**: Level 1: Foundations (Project Reactor)

## Syllabus Alignment *(mandatory)*

- **Concept**: Mastering the subscription lifecycle, manual cancellation, and custom subscribers.
- **Prerequisites**: LAB-003: Flux & Mono Foundations.
- **Learning Objectives**:
  - LO-001: Execute manual subscriptions using various `subscribe()` overloads.
  - LO-002: Control stream lifecycle using the `Disposable` interface for cancellation.
  - LO-003: Utilize `Disposables.composite()` for multi-stream resource management.
  - LO-004: Implement custom demand control using `BaseSubscriber`.
  - LO-005: Distinguish between manual `dispose()` and operator-driven lifecycle (`take`).


## Interactive Scenarios & Validation *(mandatory)*

### Scenario 1 - The Controlled Firehose (Priority: P1)

The learner will subscribe to a long-running `Flux` (e.g., `Flux.interval`) and must programmatically cancel the subscription after a certain number of items or time elapsed using the `Disposable` object.

**Validation (Automated Test)**: 
- Use `StepVerifier` with virtual time to verify cancellation.
- Alternatively, verify that the source stops emitting after `dispose()` is called.

### Scenario 2 - The Resource Manager (Priority: P1)

The learner will group multiple subscriptions into a `Disposables.composite()` (or `CompositeDisposable`). They must demonstrate that a single call to `.dispose()` on the composite correctly terminates all child streams.

**Validation (Automated Test)**: 
- Verify that multiple `Flux.interval` streams are all terminated simultaneously.

---

### Scenario 3 - The Greedy Subscriber (Priority: P1)

The learner will implement a `BaseSubscriber<T>` that manages demand by requesting items one by one (`request(1)`).

**Validation (Automated Test)**:
- Verify manual request loop.

---

### Scenario 4 - Automatic vs. Manual Lifecycle (Priority: P2)

The learner will compare manual `dispose()` with the `take(n)` operator. They must observe how `take(n)` automatically sends a cancellation signal upstream once the limit is reached.

**Validation (Automated Test)**:
- Verify that `take(n)` triggers the `doOnCancel` or `doFinally` hooks without manual intervention.

---

### Scenario 5 - The Lifecycle Watcher (Priority: P2)


---

## Educational Requirements *(mandatory)*

### Concepts to Explain

- **EX-001**: **The Subscription Contract**: What happens internally when `subscribe()` is called.
- **EX-002**: **Disposable & Resource Management**: How to prevent memory leaks by cancelling subscriptions.
- **EX-003**: **BaseSubscriber vs Lambda**: When to use a simple lambda vs. a full subscriber implementation.
- **EX-004**: **The Final Signal**: The difference between `onComplete/onError` (terminal) and `dispose()` (manual interruption).

### Technical Requirements

- **TR-001**: Use Java 21 and Project Reactor.
- **TR-002**: All scenarios MUST be validated with `StepVerifier`.
- **TR-003**: Custom subscribers MUST extend `BaseSubscriber`.
- **TR-004**: Lab MUST include "Command Dissection" for `subscribe()`, `dispose()`, and `BaseSubscriber`.

## Success Criteria *(measurable outcomes)*

- **SC-001**: Learner correctly cancels a stream and stops resource consumption.
- **SC-002**: Learner successfully implements a manual request loop in `BaseSubscriber`.
- **SC-003**: All validation tests pass.
