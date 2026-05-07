# Lab Specification: LAB-005: Essential Transformation & Filtering

**Feature Branch**: `005-essential-operators`
**Created**: 2026-05-03
**Status**: AUDITED
**Syllabus Section**: Level 1: Foundations (Project Reactor)

## Syllabus Alignment *(mandatory)*

- **Concept**: mastering synchronous and asynchronous transformation, filtering, and slice operators.
- **Prerequisites**: LAB-004: Subscriptions, Lifecycle & Side Effects.
- **Learning Objectives**:
  - LO-001: Implement 1-to-1 data transformations using `map`.
  - LO-002: Filter emissions based on predicates using `filter`.
  - LO-003: Understand asynchronous flattening with `flatMap`, `concatMap`, and `switchMap`.
  - LO-004: Apply **Filtering & Slice** operators (`take`, `skip`, `distinct`).
  - LO-005: Perform basic **Flux-to-Mono** transitions using `collectList`.
  - LO-006: Optimize collection flattening with `flatMapIterable`.

## Interactive Scenarios & Validation *(mandatory)*

### Scenario 1 - The Async User Enrichment (Priority: P1)
Given a stream of User IDs, the learner must fetch full User details using `flatMap`.
**Validation**: Verify that details are emitted, potentially interleaved.

### Scenario 2 - The Ordered Command Runner (Priority: P1)
Given a stream of tasks, ensure they are executed strictly in order using `concatMap`.
**Validation**: Verify perfect order preservation.

### Scenario 3 - The Search Debouncer (Priority: P1)
Given a rapid stream of keyboard inputs, use `switchMap` to cancel pending search requests.
**Validation**: Verify only the results for the LAST input are emitted.

### Scenario 4 - The Data Slicer (Priority: P2)
Given a stream of measurements, the learner must:
1. Skip the first 2 "warm-up" items.
2. Take only the next 5 items.
3. Remove any duplicate values using `distinct`.
**Validation**: Verify exact items in the resulting stream.

### Scenario 5 - The Batch Collector (Priority: P2)
Convert a `Flux` of items into a `Mono<List<T>>` using `collectList`.
**Validation**: Verify the resulting list size and content.

---

## Educational Requirements *(mandatory)*

### Concepts to Explain
- **EX-001**: **Flattening**: Why nested `Publishers` require flattening operators.
- **EX-002**: **Slicing the Stream**: How `take` and `skip` allow processing of specific segments.
- **EX-003**: **Uniqueness**: How `distinct` tracks state to filter duplicates.
- **EX-004**: **Flux to Mono**: The concept of "Aggregation" into a single container.

### Technical Requirements
- **TR-001**: Use Java 21 and Project Reactor.
- **TR-002**: Use `StepVerifier` for all validations.
- **TR-003**: Use `PublisherProbe` to verify cancellation signals.
- **TR-004**: README MUST include an **Interactive Self-Assessment**.
- **TR-005**: Provide "Command Dissection" for `flatMap` vs `concatMap`.

## Success Criteria
- **SC-001**: Correct choice of operator based on ordering and filtering requirements.
- **SC-002**: Successful implementation of slicing logic (`skip`, `take`, `distinct`).
- **SC-003**: Successful conversion of Flux to Mono via `collectList`.
- **SC-004**: All validation tests pass.
