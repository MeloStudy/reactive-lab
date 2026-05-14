# Lab Specification: LAB-009: Backpressure Strategies & Rate Limiting

**Feature Branch**: `009-backpressure-strategies`
**Created**: 2026-05-04
**Status**: AUDITED
**Syllabus Section**: Level 2: The Core Spec & Advanced Control

## Syllabus Alignment *(mandatory)*

- **Concept**: Controlling flow and demand in high-throughput pipelines.
- **Prerequisites**: LAB-007 (Threading), LAB-008 (Hot/Cold).
- **Learning Objectives**:
  - LO-001: Visualize and reproduce a `BackpressureOverflowException`.
  - LO-002: Apply **Overflow Strategies**: `onBackpressureBuffer`, `onBackpressureDrop`, `onBackpressureLatest`.
  - LO-003: Implement **Rate Limiting** using `limitRate` to control prefetch.
  - LO-004: Implement **Request Limiting** using `limitRequest`.
  - LO-005: Use `buffer()` and `window()` as grouping-based flow control.

## Interactive Scenarios & Validation *(mandatory)*

### Scenario 1 - The Overflowing Producer (Priority: P1)
A fast producer (`Sinks`) generates events faster than a slow consumer can process.
**Validation**: Reproduce `OverflowException` and fix it using `onBackpressureBuffer(size)`.

### Scenario 2 - Real-time Data Priority (Priority: P1)
In a sensor data scenario, discard old data using `onBackpressureLatest`.
**Validation**: Verify that the subscriber only receives the newest values when falling behind.

### Scenario 3 - The Polite Subscriber (Rate Limiting) (Priority: P1)
An upstream source is capable of millions of events, but a downstream API has a strict quota.
**Validation**: Use `limitRate(10)` and verify that the upstream only receives requests in batches of 10.

### Scenario 4 - The Hard Stop (Request Limiting) (Priority: P2)
Stop the stream after exactly `N` items have been requested, regardless of source size, using `limitRequest(n)`.
**Validation**: Verify the stream completes after exactly `N` items.

---

## Educational Requirements *(mandatory)*

### Concepts to Explain
- **EX-001**: **Demand-Driven Flow**: How the Subscriber's `request(n)` signal travels upstream.
- **EX-002**: **Overflow Strategies**: Buffer (Memory) vs Drop (Data Loss) vs Latest (Relevance).
- **EX-003**: **Rate Limiting vs Schedulers**: Why `limitRate` is about *demand* and not just *timing*.
- **EX-004**: **Prefetch Optimization**: How `limitRate` adjusts the default prefetch size (256).

### Technical Requirements
- **TR-001**: Use Java 21 and Project Reactor.
- **TR-002**: Use `StepVerifier` with `request(n)` assertions.
- **TR-003**: Use `PublisherProbe` to verify upstream request signals.
- **TR-004**: **Traceable Implementation**: README MUST include relative links (from the project root) to Java classes and tests for each scenario.

## Success Criteria
- SC-001: Successful handling of high-throughput stream without crashing.
- SC-002: Correct application of `limitRate` to manage downstream quota.
- SC-003: All validation tests pass.
