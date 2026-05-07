# Implementation Plan: LAB-006: Combining & Aggregation Operators

**Branch**: `006-combining-errors` | **Date**: 2026-05-03
**Input**: Specification from `/docs/specs/006-combining-errors/spec.md`

## Phase 1: Project Setup
1. Create `labs/006-combining-errors`.
2. Package: `com.reactivelab.orchestration`.

## Phase 2: Combination Operators
1. **Scenario 1 (Zip)**: Implement `DashboardService`. 
2. **Scenario 2 (Merge/Concat)**: Implement `SocialFeedService`. 

## Phase 3: Accumulation & Reduction (New)
1. **Scenario 3 (Scan)**: Implement `TransactionTracker` to calculate running balances.
2. **Scenario 4 (Reduce)**: Implement `TotalCalculator` for final sums.
3. **Test**: Verify intermediate values for `scan` and terminal values for `reduce`.

## Phase 4: Batching & Windowing (New)
1. **Scenario 5 (Buffer)**: Implement `BatchProcessor` for list-based batching.
2. **Scenario 6 (Window)**: Implement `WindowProcessor` for stream-based partitioning.
3. **Test**: Verify container types (`List` vs `Flux`).

## Phase 5: Resilience & Error Handling
1. **Scenario 7 (Resilience)**: Implement `ResilientClient` using `onErrorResume` and `retry`.

## Phase 6: Documentation & Assessment
1. **CONCEPT.md**: Explain the difference between `scan` (Stateful) and `reduce` (Terminal).
2. **README.md**: 
   - Command Dissection for `scan`, `buffer`, and `zip`.
   - Updated **Knowledge Check** section.

## Constitution Compliance Check
- [x] Java 21+ syntax.
- [x] `StepVerifier` for everything.
- [x] Self-Assessment quiz included.
