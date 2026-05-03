# Lab Specification: LAB-001: The Reactive Manifesto & Asynchronous Paradigms

**Feature Branch**: `001-reactive-manifesto`
**Created**: 2026-04-30
**Status**: Audited
**Syllabus Section**: Level 1: The Reactive Mindset & Foundations (Project Reactor)

## Syllabus Alignment *(mandatory)*

- **Concept**: The Reactive Manifesto & Asynchronous Paradigms (Responsiveness, Resilience, Elasticity, Message Driven).
- **Prerequisites**: None (Introductory Lab).
- **Learning Objectives**:
  - LO-001: Define and identify the 4 pillars of the Reactive Manifesto in a system architecture.
  - LO-002: Contrast Callbacks, Promises, and Observables as evolution of asynchronous handling.
  - LO-003: Understand the lifecycle of a Reactive Stream (onNext, onError, onComplete).
  - LO-004: Contrast the Event Loop execution model with the traditional Blocking Thread model (Servlet stack).

## Interactive Scenarios & Validation *(mandatory)*

### Scenario 1 - The Pillars of Reactive Systems (Priority: P1)

The learner will be presented with a "broken" asynchronous system (conceptual) that fails under load or errors. They must identify which Reactive pillar is missing and explain how a Message-Driven approach improves Resilience.

**Validation**: Conceptual walkthrough identifying architectural gaps.

**Acceptance Scenarios**:

1. **Given** a failing callback-based system, **When** the learner implements a message-driven approach, **Then** the system remains Responsive under simulated error conditions.

---

### Scenario 2 - From Single to Multiple: Futures vs Streams (Priority: P1)

The learner will differentiate between a `CompletableFuture` (returning one value) and a `Flux` (returning a stream of items over time).

**Validation**: Self-assessment check on cardinality and lazy execution.

---

### Scenario 3 - Visualizing Elasticity & Backpressure (Priority: P2)

The learner will analyze a "Fast Producer" sending a burst of messages to a "Slow Consumer". They must identify the bottleneck and explain how Backpressure prevents system failure.

**Validation**: Identification of flow control mechanisms in a reactive architecture.

---

## Educational Requirements *(mandatory)*

### Concepts to Explain

- **EX-001**: The Reactive Manifesto (4 Pillars).
- **EX-002**: Evolution of Asynchrony: Callbacks -> Promises -> Observables.
- **EX-003**: The Push-based model vs Pull-based model.

### Technical Requirements

- **TR-001**: Theoretical context MUST be provided in a `CONCEPT.md` file.
- **TR-002**: Lab MUST include an interactive "Manifesto Check" in the README.
- **TR-003**: All technical terms must align with Project Reactor and JVM thread models.

## Success Criteria *(measurable outcomes)*

- **SC-001**: Learner correctly identifies missing pillars in a failing architecture.
- **SC-002**: Learner successfully differentiates between a Future and a Stream.

## Assumptions

- Basic understanding of Java (JDK 21).
