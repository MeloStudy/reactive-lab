# Lab Specification: LAB-000 The Reactive Mindset & Foundational Analogies

**Feature Branch**: `000-reactive-mindset`
**Created**: 2026-04-30
**Status**: Draft
**Syllabus Section**: Level 1: The Reactive Mindset & Foundations

## Syllabus Alignment *(mandatory)*

- **Concept**: Foundational shift from Imperative/Pull to Reactive/Push. Understanding streams through analogies.
- **Prerequisites**: None. This is the entry point.
- **Learning Objectives**:
  - LO-001: Internalize the "Push" vs "Pull" data delivery models.
  - LO-002: Understand the "Excel Analogy" for reactive data propagation.
  - LO-003: Differentiate between Declarative and Imperative programming styles in the context of streams.
  - LO-004: Identify scenarios where Reactive Programming adds value and where it adds unnecessary complexity.
  - LO-005: Understand I/O Blocking vs Non-blocking and its impact on scalability.
  - LO-006: Recognize Errors as first-class citizens (signals) in a stream rather than exceptional flow-breakers.

## Interactive Scenarios & Validation *(mandatory)*

### Scenario 1 - The Mental Model: From Variable to Stream (Priority: P1)

This scenario will involve a simple comparison between a standard variable assignment and a reactive stream emission in RxJS.

**Validation (Automated Test)**: A simple Node.js test (Jest) verifying that a stream emission triggers an effect, whereas a variable change (without observer) does not.

**Acceptance Scenarios**:

1. **Given** a reactive stream and a standard variable, **When** the underlying data source changes, **Then** the stream observer reacts while the standard variable remains stale unless manually reassigned.

---

### Scenario 2 - Handling the "Unpleasant": Errors and Backpressure (Priority: P2)

Show how an error in a stream is just another signal and how a "fast producer" can be managed conceptually (Backpressure) to maintain Elasticity.

**Validation (Automated Test)**: Jest test verifying that an error signal (`onError`) is caught and handled without crashing the process, and a conceptual check on Backpressure.

---

## Educational Requirements *(mandatory)*

### Concepts to Explain

- **EX-001**: **The Pain Points of Imperative Programming**: Thread-per-request exhaustion (Java's classic model), Callback Hell, and the complexity of managing state in `CompletableFuture` chains.
- **EX-002**: **I/O Blocking & The Wait State**: Explaining why waiting for I/O is the biggest bottleneck in modern applications, comparing a "Sleeping Thread" (Java) vs the "Idle Waiter" (Reactive).
- **EX-003**: **The Reactive Solution**: How delegation and non-blocking I/O solve the "wait" problem. Mentioning the transition from Servlet-stack (Spring MVC) to Reactive-stack (Spring WebFlux) at a high level.
- **EX-004**: **Errors as Signals**: Transitioning from `try-catch` blocks to `onError` stream notifications.
- **EX-005**: **Elasticity & Backpressure**: Why a system can't be truly elastic if it can't tell a producer to "slow down".
- **EX-006**: **Analogies**: 
    - **The Excel Spreadsheet**: Automatic recalculation.
    - **The Assembly Line**: Backpressure at the station.
    - **The Restaurant**: The Waiter (Event Loop) vs The Chef per Table (Thread-per-request).
    - **The Post Office**: Letters (Events) arriving in a mailbox (Buffer) vs a Phone Call (Blocking).

### Technical Requirements

- **TR-001**: Lab infrastructure must be containerized (though for this lab it might be minimal).
- **TR-002**: Lab README MUST provide native orchestration (npm test).
- **TR-003**: Lab MUST include automated validation tests (Node.js/Jest).
- **TR-006**: Theoretical context MUST be provided in a `CONCEPT.md` file.

## Success Criteria *(measurable outcomes)*

- **SC-001**: Learner successfully differentiates between a blocking and a non-blocking sequence.
- **SC-002**: All validation tests pass for the "Push vs Pull" and "Error handling" demonstrations.

## Assumptions

- Learner has basic knowledge of JavaScript/Node.js.
- Docker and Node.js are installed.
