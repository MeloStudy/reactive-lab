# Lab Specification: LAB-006: The Reactive Streams Specification & TCK

**Feature Branch**: `006-reactive-streams`
**Created**: 2026-04-30
**Status**: AUDITED
**Syllabus Section**: Level 2: The JVM Reactive Core (Java 21 & Project Reactor)

## Syllabus Alignment *(mandatory)*

- **Concept**: The Reactive Streams Specification (Publisher, Subscriber, Subscription, Processor) and the TCK (Technology Compatibility Kit).
- **Prerequisites**: LAB-001 (Conceptually).
- **Learning Objectives**:
  - LO-001: Implement the core Reactive Streams interfaces from scratch (Publisher, Subscriber).
  - LO-002: Master the "Handshake" lifecycle: `onSubscribe` -> `request(n)` -> `onNext`.
  - LO-003: Understand the "Demand" model (Push vs. Pull-based Backpressure).
  - LO-004: Validate a custom Publisher against the official Reactive Streams TCK.
  - LO-005: Trace an implementation failure back to a specific rule in the Reactive Streams specification (Rule 1.1, 3.9, etc.).

## Interactive Scenarios & Validation *(mandatory)*

### Scenario 1 - The Raw Implementation (Priority: P1)

The learner will implement a `CustomPublisher` that emits a sequence of integers. Instead of using Reactor's `Flux`, they must implement the raw `Publisher` interface to understand the internal contract.

**Validation (Automated Test)**: A JUnit 5 test that uses a custom `Subscriber` to verify that no data is received until `request(n)` is called.

**Acceptance Scenarios**:
1. **Given** a CustomPublisher, **When** a Subscriber connects, **Then** `onSubscribe` is called exactly once.
2. **Given** a subscription, **When** no demand is requested, **Then** no data is emitted (Cold start/Backpressure).

---

### Scenario 2 - The TCK Challenge (Priority: P1)

The learner will attempt to pass a subset of the **Reactive Streams TCK (Technology Compatibility Kit)**. This is the ultimate proof of specification compliance.

**Validation (Automated Test)**: Integration of `org.reactivestreams:reactive-streams-tck` to validate the `CustomPublisher`.

---

## Educational Requirements *(mandatory)*

### Concepts to Explain

- **EX-001**: Why a Specification? Cross-library compatibility (Reactor vs. RxJava vs. Akka).
- **EX-002**: The 4 Interfaces: `Publisher`, `Subscriber`, `Subscription`, `Processor`.
- **EX-003**: Dynamic Push/Pull: How `request(n)` solves the buffer overflow problem.

### Technical Requirements

- **TR-001**: Project MUST be managed via **Maven** (pom.xml).
- **TR-002**: MUST use **Java 21** and `java.util.concurrent.Flow` or the `org.reactivestreams` interfaces.
- **TR-003**: Lab MUST include automated validation tests via **JUnit 5**.
- **TR-004**: MUST include the official **Reactive Streams TCK** as a dependency.

## Success Criteria *(measurable outcomes)*

- **SC-001**: Learner's Publisher passes at least the core TCK tests (e.g., must signal `onNext` only after request).
- **SC-002**: Learner successfully demonstrates a "Cancel" operation midway through a stream.
