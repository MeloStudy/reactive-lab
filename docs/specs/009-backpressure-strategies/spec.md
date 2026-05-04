# Lab Specification: LAB-009: Backpressure Strategies & Flow Control [READY]

**Feature Branch**: `009-backpressure-strategies`
**Created**: 2026-05-04
**Status**: Audited
**Syllabus Section**: Level 2: The Core Spec & Advanced Control

## Syllabus Alignment *(mandatory)*

- **Concept**: Backpressure Strategies (`buffer`, `window`, `onBackpressureDrop`, `onBackpressureBuffer`).
- **Prerequisites**: LAB-007 (Threading Models), LAB-008 (Programmatic Generation).
- **Learning Objectives**:
  - LO-001: Visualize and reproduce a `BackpressureOverflowException`.
  - LO-002: Apply `onBackpressureBuffer` to handle spikes without data loss.
  - LO-003: Apply `onBackpressureDrop` and `onBackpressureLatest` for lossy but responsive systems.
  - LO-004: Use `Sinks.Many` to simulate "Push" producers that disregard consumer demand.
  - LO-005: Use `buffer()` and `window()` as higher-level flow control mechanisms.

## Interactive Scenarios & Validation *(mandatory)*

### Scenario 1 - The Overflowing Producer (P1)

A fast producer (implemented via `Sinks.many().multicast().directBestEffort()`) generates events at a rate higher than the consumer can process. The learner must configure a buffer strategy to prevent the system from crashing.

**Validation (Automated Test)**: `StepVerifier` test that simulate a slow subscriber (e.g., using `delayElements`) and verifies that a specific amount of data is buffered without an error signal.

**Acceptance Scenarios**:

1. **Given** a `Flux.range(1, 1000)` producer, **When** a subscriber with a slow processing rate is attached without backpressure handling, **Then** the stream should fail with `OverflowException`.
2. **Given** the same producer, **When** `onBackpressureBuffer(100)` is applied, **Then** the stream should process the first 100 elements and fail gracefully or complete if the buffer is sufficient.

---

### Scenario 2 - Real-time Data Priority (P2)

In a UI or sensor data scenario, only the most recent data matters. The learner must implement a dropping strategy where old data is discarded in favor of keeping the system responsive.

**Validation (Automated Test)**: `StepVerifier` test verifying that only the most recent elements (or a specific subset) are received by the subscriber when the producer is too fast.

---

## Educational Requirements *(mandatory)*

### Concepts to Explain

- **EX-001**: The "Push" vs "Pull" model and why "Push" causes overflow.
- **EX-002**: Buffer vs Drop vs Latest: When to use which?
- **EX-003**: Windowing: Transforming a stream of elements into a stream of streams.

### Technical Requirements

- **TR-001**: Lab infrastructure MUST be containerized strictly using **Docker / Docker Compose**.
- **TR-002**: Lab README MUST provide native orchestration and execution commands (e.g., `mvn test`) step-by-step.
- **TR-003**: Lab MUST include automated validation tests (Java/JUnit for Reactor/WebFlux labs).
- **TR-004**: Reactive signals (`onNext`, `onError`, `onComplete`) MUST be explicitly validated in tests (e.g., using `StepVerifier`).
- **TR-005**: Lab README MUST provide a "Command Dissection" for any new operator or CLI flag introduced.
- **TR-006**: Theoretical context MUST be provided in a `CONCEPT.md` file.

## Success Criteria *(measurable outcomes)*

- **SC-001**: Learner successfully handles an 10,000 msg/sec stream with a 100 msg/sec consumer using various strategies.
- **SC-002**: All validation tests pass upon completion.

## Assumptions

- Learner understands `publishOn` and how it decouples producer/consumer rates.
- Java 21+ and Maven are installed.
