# Lab Specification: LAB-001: The Reactive Manifesto & Asynchronous Paradigms

**Feature Branch**: `001-reactive-manifesto`
**Created**: 2026-04-30
**Status**: Audited
**Syllabus Section**: Level 1: The Reactive Mindset & Foundations (Node.js & RxJS)

## Syllabus Alignment *(mandatory)*

- **Concept**: The Reactive Manifesto & Asynchronous Paradigms (Responsiveness, Resilience, Elasticity, Message Driven).
- **Prerequisites**: None (Introductory Lab).
- **Learning Objectives**:
  - LO-001: Define and identify the 4 pillars of the Reactive Manifesto in a system architecture.
  - LO-002: Contrast Callbacks, Promises, and Observables as evolution of asynchronous handling.
  - LO-003: Implement a basic RxJS Observable that emits multiple values over time.
  - LO-004: Contrast the Event Loop execution model with the traditional Blocking Thread model to justify the reactive approach.

## Interactive Scenarios & Validation *(mandatory)*

### Scenario 1 - The Pillars of Reactive Systems (Priority: P1)

The learner will be presented with a "broken" asynchronous system (using basic callbacks) that fails under load or errors. They must identify which Reactive pillar is missing and refactor a small snippet to use Message-Driven principles to improve Resilience.

**Validation (Automated Test)**: Jest test verifying that the refactored logic handles errors without crashing the main process (Resilience) and responds within a timeout (Responsiveness).

**Acceptance Scenarios**:

1. **Given** a failing callback-based system, **When** the learner implements a message-driven approach, **Then** the system remains Responsive under simulated error conditions.

---

### Scenario 2 - From Single to Multiple: Promises vs Observables (Priority: P1)

The learner will refactor a data fetcher that currently uses a Promise (returning one value) to an RxJS Observable (returning a stream of progress updates + final data).

**Validation (Automated Test)**: Jest test using `rxjs/testing` TestScheduler or `toPromise` to verify multiple `onNext` signals before completion.

---

### Scenario 3 - Visualizing Elasticity & Backpressure (Priority: P2)

The learner will execute a manual script that simulates a "Fast Producer" sending a burst of messages to a "Slow Consumer". They must identify the bottleneck and observe how the system handles the overflow (simulated visual buffer).

**Validation (Manual)**: Execution of `src/scenarios/elasticity.js` and observation of logs showing the decoupling of production and consumption.

---

## Educational Requirements *(mandatory)*

### Concepts to Explain

- **EX-001**: The Reactive Manifesto (4 Pillars).
- **EX-002**: Evolution of Asynchrony: Callbacks -> Promises -> Observables.
- **EX-003**: The Push-based model vs Pull-based model.

### Technical Requirements

- **TR-001**: Lab infrastructure MUST be containerized strictly using **Docker / Docker Compose**.
- **TR-002**: Lab README MUST provide native orchestration and execution commands (e.g., `npm test`) step-by-step.
- **TR-003**: Lab MUST include automated validation tests (Node.js/Jest for RxJS labs).
- **TR-004**: Reactive signals (`onNext`, `onError`, `onComplete`) MUST be explicitly validated in tests.
- **TR-005**: Lab README MUST provide a "Command Dissection" for any new operator or CLI flag introduced.
- **TR-006**: Theoretical context MUST be provided in a `CONCEPT.md` file.
- **TR-007**: Lab MUST explicitly instruct "Atomic Cleanup" via native commands.

## Success Criteria *(measurable outcomes)*

- **SC-001**: Learner successfully implements an Observable that emits progress events and a final result.
- **SC-002**: All validation tests pass, confirming the learner understood the difference between single and multi-value streams.

## Assumptions

- Learner understands basic JavaScript (ES6+).
- Docker Desktop and Node.js v20+ are installed.
