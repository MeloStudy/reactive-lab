# Implementation Plan: LAB-006 Refinement

This plan details the steps to align LAB-006 with the latest syllabus, including advanced aggregation, grouping, and resource safety.

## 1. Refactor Existing Components
- Fix scenario numbering in Javadocs for:
  - `DashboardService` (Scenario 1)
  - `SocialFeedService` (Scenario 2)
  - `TransactionTracker` (Scenario 3)
  - `TotalCalculator` (Scenario 4)
  - `BatchProcessor` (Scenario 5)
  - `WindowProcessor` (Scenario 6)
  - `ResilientClient` (Scenario 7)
  - `ReliableService` (Scenario 8)

## 2. New Feature Implementation
- **AnalyticsProcessor.java**:
  - Implement `collectMap` (e.g., mapping user ID to Profile).
  - Implement `collectSortedList` (e.g., sorting transactions by value).
  - Implement `groupBy` (Scenario 10: Grouping events by category).
- **ResourceSafetyService.java**:
  - Implement logic demonstrating `doOnDiscard` when items are filtered out or subscription is cancelled (Scenario 11).

## 3. Documentation Upgrade
- **README.md**:
  - Categorize scenarios into: **Orchestration**, **Aggregation**, **Batching & Windowing**, **Resilience**, and **Advanced Control**.
  - Add missing scenarios (8-11).
  - Update Command Dissection.
- **CONCEPT.md**:
  - Fix numbering issues.
  - Add sections for `groupBy`, `collectMap/SortedList`, and `doOnDiscard`.
  - Deepen the explanation of `retry`.

## 4. Test Suite Alignment
- Update `ResourceSafetyTest` to use the new service.
- Create `AnalyticsProcessorTest` for Scenarios 9 and 10.
- Verify all 11 scenarios with `StepVerifier`.

## 5. Final Audit
- Run all tests.
- Verify pedagogical links and consistency.
