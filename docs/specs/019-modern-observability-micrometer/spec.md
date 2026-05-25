# Lab Specification: LAB-019: Modern Observability (Micrometer Observation)

**Feature Branch**: `019-modern-observability-micrometer`
**Created**: 2026-05-24
**Status**: AUDITED
**Syllabus Section**: Level 4: Resilient & Event-Driven Systems

## Syllabus Alignment *(mandatory)*

- **Concept**: ObservationRegistry, ObservationHandler, Automatic instrumentation (WebFlux/WebClient), and Tagging strategies (High/Low cardinality).
- **Prerequisites**: LAB-011 (Reactive Web with Spring WebFlux), LAB-012 (WebClient: Orchestrating Downstream Services)
- **Learning Objectives**:
  - LO-001: Understand how Micrometer Observation API works and how it integrates with Reactor Context to propagate tracing information.
  - LO-002: Implement custom `ObservationHandler` to process observation lifecycle events.
  - LO-003: Configure high and low cardinality tags for effective metrics and distributed tracing in WebFlux and WebClient.

## Interactive Scenarios & Validation *(mandatory)*

### Scenario 1 - Instrumenting WebFlux Endpoints and WebClient (Priority: P1)

Learner will configure `ObservationRegistry` in a Spring WebFlux application and observe automatic instrumentation of incoming HTTP requests and outgoing WebClient calls.

**Validation (Automated Test)**: JUnit test using StepVerifier to call endpoints and verify that tracing context is propagated across the WebClient call, and metrics are recorded in an in-memory `TestObservationRegistry`.

**Acceptance Scenarios**:

1. **Given** a WebFlux application with Micrometer Observation configured, **When** a request is made to an endpoint that calls a downstream service via WebClient, **Then** a unified trace ID is propagated and observed in both incoming and outgoing request metrics.

---

### Scenario 2 - Custom Observation and Tagging Strategy (Priority: P2)

Learner will create a custom `Observation` around a specific reactive service method, adding custom high/low cardinality keys (e.g., user ID vs request type).

**Validation (Automated Test)**: JUnit test using `TestObservationRegistry` to assert that the custom observation was started, stopped, and contains the expected low and high cardinality KeyValues.

---

## Educational Requirements *(mandatory)*

### Concepts to Explain

- **EX-001**: The difference between Metrics and Tracing, and how Micrometer Observation unifies them.
- **EX-002**: Reactor Context and its role in propagating `Observation` across asynchronous thread boundaries in WebFlux.
- **EX-003**: High vs. Low Cardinality tags (when to use which, and the implications on memory/TSDB storage).

### Technical Requirements

- **TR-001**: Lab infrastructure (e.g., brokers, databases) MUST be containerized strictly using **Docker / Docker Compose**.
- **TR-002**: Lab README MUST provide native orchestration and execution commands (e.g., `mvn test`) step-by-step. Bash scripts as wrappers are PROHIBITED.
- **TR-003**: Lab MUST include automated validation tests (Java/JUnit for Reactor/WebFlux labs).
- **TR-004**: Reactive signals (`onNext`, `onError`, `onComplete`) MUST be explicitly validated in tests (e.g., using `StepVerifier`).
- **TR-005**: Lab README MUST provide a "Command Dissection" for any new operator or CLI flag introduced.
- **TR-006**: Theoretical context (Event Loop, Reactive Streams API) MUST be provided in a `CONCEPT.md` file.
- **TR-007**: Lab MUST explicitly instruct "Atomic Cleanup" via native commands.
- **TR-008**: An optional Makefile MAY be provided strictly as an automated shortcut container.

## Success Criteria *(measurable outcomes)*

- **SC-001**: Learner successfully instrument an end-to-end reactive application with WebFlux and WebClient, producing unified traces and metrics.
- **SC-002**: All validation tests (including `TestObservationRegistry` assertions) pass upon completion.

## Assumptions

- Learner understands basic Spring WebFlux and WebClient usage.
- Docker Desktop and JDK 21+ are installed.
