# Lab Specification: LAB-006: Combining & Aggregation Operators

**Feature Branch**: `006-combining-errors`
**Created**: 2026-05-03
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
  - LO-008: Handle errors and recovery using `onErrorResume` and `retry`.

## Interactive Scenarios & Validation *(mandatory)*

### Scenario 1 - The Dashboard Aggregator (Priority: P1)
Combine a User profile (`Mono`) with their Friends count (`Mono`) using `zip`.
**Validation**: Verify consolidated header string.

### Scenario 2 - The Running Balance (Accumulation) (Priority: P1)
Given a stream of transaction amounts, calculate the **running balance** after each transaction using `scan`.
**Validation**: Verify each intermediate balance emission.

### Scenario 3 - The Final Total (Reduction) (Priority: P1)
Calculate the **total sum** of a completed stream of numbers using `reduce`.
**Validation**: Verify the final `Mono<Integer>` result.

### Scenario 4 - The Batch Processor (Priority: P1)
Group a stream of items into batches of 5 for bulk processing using `buffer(5)`.
**Validation**: Verify that each emitted item is a `List` of size 5.

### Scenario 5 - The Windowed Stream (Priority: P2)
Split a high-frequency stream into "windows" based on count or time using `window`.
**Validation**: Verify that it emits `Flux<Flux<T>>` and each inner flux has the expected size.

### Scenario 6 - Basic Resilience (Priority: P1)
Implement a fallback using `onErrorResume` when a combination source fails.
**Validation**: Verify switch to fallback source.

---

## Educational Requirements *(mandatory)*

### Concepts to Explain
- **EX-001**: **Eager vs Lazy Combination**: `merge` vs `concat`.
- **EX-002**: **Accumulation vs Reduction**: Why `scan` emits every step while `reduce` only emits the final result.
- **EX-003**: **Batching vs Windowing**: The difference between `List` containers (`buffer`) and sub-streams (`window`).
- **EX-004**: **Collecting into Structures**: `collectMap` and `collectSortedList`.

### Technical Requirements
- **TR-001**: Use Java 21 and Project Reactor.
- **TR-002**: Use `StepVerifier` for all validations.
- **TR-003**: README MUST include an **Interactive Self-Assessment**.

## Success Criteria
- **SC-001**: Successful orchestration of multiple sources.
- **SC-002**: Correct implementation of running totals (`scan`) and final totals (`reduce`).
- **SC-003**: Efficient batching using `buffer`.
- **SC-004**: All validation tests pass.
