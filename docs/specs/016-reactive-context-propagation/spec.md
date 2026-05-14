# Lab Specification: LAB-016: Reactive Context & Tracing Propagation

**Feature Branch**: `016-reactive-context-propagation`
**Created**: 2026-05-05
**Status**: AUDITED
**Syllabus Section**: Level 3: Enterprise Reactive Services (Spring WebFlux)

## Syllabus Alignment *(mandatory)*

- **Concept**: `Reactor Context` for metadata propagation.
- **Prerequisites**: LAB-011 (WebFlux), LAB-007 (Threading Models).
- **Learning Objectives**:
  - LO-001: Contrast `ThreadLocal` storage with Reactor's `Context` in non-blocking environments.
  - LO-002: Master `contextWrite` and `deferContextual` operators for metadata management.
  - LO-003: Implement MDC (Mapped Diagnostic Context) propagation for distributed tracing.
  - LO-004: Understand the immutable and "upward" propagation characteristics of Context.
  - LO-005: Evaluate architectural trade-offs between Java 21+ Scoped Values and Reactor Context.

## Interactive Scenarios & Validation *(mandatory)*

### Scenario 1 - The Correlation ID Journey (Priority: P1)

Learners will implement a mechanism to capture a `X-Correlation-ID` from an incoming request (simulated) and propagate it through several asynchronous transformations, ensuring that every log statement emitted by the pipeline includes this ID.

**Validation (Automated Test)**: A JUnit test using `StepVerifier` that asserts the presence of the Correlation ID in the Context at various stages and verifies that a mock appender captured the ID in the MDC.

**Acceptance Scenarios**:

1. **Given** a reactive pipeline initialized with `contextWrite(Context.of("correlationId", "12345"))`, **When** using `deferContextual` to access the view, **Then** the log output must contain `[12345]`.

---

### Scenario 2 - Propagating Security State (Priority: P2)

Learners will simulate a security filter that injects a `User` object into the Reactor Context and verify that a downstream "Service" can retrieve this user without explicit parameter passing.

**Validation (Automated Test)**: A test case where a `Mono` pipeline is executed with a security context, and a downstream component fails if the user is missing from the Context view.

---

## Educational Requirements *(mandatory)*

### Concepts to Explain

- **EX-001**: **ThreadLocal vs. Context**: Why `ThreadLocal` fails in an Event Loop model where one thread handles multiple requests.
- **EX-002**: **Context Propagation Flow**: Explaining why `contextWrite` flows *upstream* (from the subscriber towards the source).

### Technical Requirements

- **TR-001**: Lab infrastructure MUST be containerized strictly using **Docker / Docker Compose** (if any external tracing sink like Zipkin is used, though for this lab internal MDC is sufficient).
- **TR-002**: Lab README MUST provide native orchestration and execution commands (e.g., `mvn test`).
- **TR-003**: Lab MUST include automated validation tests (Java/JUnit).
- **TR-004**: Reactive signals (`onNext`, `onError`, `onComplete`) MUST be explicitly validated.
- **TR-005**: Lab README MUST provide a "Command Dissection" for `contextWrite`, `deferContextual`, and `ContextView`.
- **TR-006**: Theoretical context MUST be provided in a `CONCEPT.md` file.
- **TR-007**: Lab README MUST include an **Interactive Self-Assessment** with collapsible `<details>` blocks.
- **TR-008**: An optional Makefile MAY be provided strictly as an automated shortcut container.

## Success Criteria *(measurable outcomes)*

- **SC-001**: Learner successfully propagates a custom key through a pipeline spanning multiple `Schedulers`.
- **SC-002**: All validation tests pass, confirming MDC integration works without leaking state between threads.
- **SC-003**: Learner provides Traceable Implementation links for all scenarios to connect theory directly to the Java implementation.

## Assumptions

- Learner is familiar with Spring WebFlux and Project Reactor threading (Schedulers).
- Java 21 and Maven are installed.
