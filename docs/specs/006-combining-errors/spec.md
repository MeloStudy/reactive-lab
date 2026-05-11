# Lab Specification: LAB-006: Combining & Aggregation Operators

**Feature Branch**: `006-combining-errors`
**Created**: 2026-05-03
**Last Updated**: 2026-05-11
**Status**: AUDITED
**Syllabus Section**: Level 1: Foundations (Project Reactor)

## Syllabus Alignment *(mandatory)*

- **Concept**: Orchestrating multiple streams and aggregating data over time or into structures.
- **Prerequisites**: LAB-005: Essential Transformation & Filtering.
- **Learning Objectives**:
  - LO-001: Combine streams eagerly with `merge` (interleaving).
  - LO-002: Combine streams sequentially with `concat`.
  - LO-003: Pair elements from different sources with `zip`.
  - LO-004: **Accumulate state** over time using `scan` (emitting intermediates).
  - LO-005: **Reduce** a stream into a single final value with `reduce`.
  - LO-006: **Batch items** into collections using `buffer`.
  - LO-007: **Partition items** into sub-streams using `window`.
  - LO-008: Handle errors and recovery using `onErrorResume`, `onErrorReturn`, and `retry`.
  - LO-009: Implement **Resource Safety** and handle dropped elements with `doOnDiscard`.
  - LO-010: Group elements into sub-streams by a key using `groupBy`.

## Interactive Scenarios & Validation *(mandatory)*

### Scenario 1 - The Dashboard Aggregator (P1)
Combine User profile (`Mono`) with Friends count (`Mono`) using `zip`.

### Scenario 2 - The Social Feed Aggregator (P1)
Fetch multiple feeds simultaneously using `merge` vs sequentially using `concat`.

### Scenario 3 - The Running Balance (P1)
Calculate the cumulative sum of transactions using `scan`.

### Scenario 4 - The Final Total (P1)
Calculate the grand total of a completed stream using `reduce`.

### Scenario 5 - The Batch Processor (P1)
Group high-frequency items into `List` batches using `buffer`.

### Scenario 6 - The Windowed Stream (P2)
Split a stream into sub-fluxes using `window` for parallel window processing.

### Scenario 7 - The Resilient Client (P1)
Implement a recovery ladder using `onErrorReturn`, `onErrorMap`, and `onErrorResume`.

### Scenario 8 - The Reliable Service (P2)
Implement transient error recovery with `retry`.

### Scenario 9 - The Advanced Analytics (P2)
Aggregate a stream into lookups using `collectMap` and `collectSortedList`.
[[Code]](src/main/java/com/reactivelab/orchestration/CollectionProcessor.java)

### Scenario 10 - The Event Grouper (P2)
Group a stream of diverse events by their type using `groupBy`.
[[Code]](src/main/java/com/reactivelab/orchestration/EventGrouper.java)

### Scenario 11 - Resource Safety (P3)
Demonstrate `doOnDiscard` to prevent leaks when items are filtered or cancelled.

---

## Educational Requirements *(mandatory)*

### Concepts to Explain
- **EX-001**: **Eager vs Lazy Combination**: `merge` vs `concat`.
- **EX-002**: **Accumulation vs Reduction**: `scan` (intermediates) vs `reduce` (terminal).
- **EX-003**: **Batching vs Windowing vs Grouping**: `buffer` (Lists) vs `window` (Fluxes) vs `groupBy` (keyed Fluxes).
- **EX-004**: **Collecting into Structures**: How `collectMap` and `collectSortedList` differ from simple list collection.
- **EX-005**: **Resource Safety**: The importance of `doOnDiscard` in stateful or buffered pipelines.

### Technical Requirements
- **TR-001**: Use Java 21 and Project Reactor.
- **TR-002**: Use `StepVerifier` for all validations.
- **TR-003**: README MUST include an **Interactive Self-Assessment**.

## Success Criteria
- **SC-001**: Correct orchestration of multiple asynchronous sources.
- **SC-002**: Accurate aggregation (running totals, final totals, collections).
- **SC-003**: Robust error recovery and retry logic.
- **SC-004**: Proper resource management and grouping.
- **SC-005**: All 11 scenario tests pass.
