# Lab Specification: LAB-020: Reactive Messaging with Apache Kafka

**Feature Branch**: `020-reactor-kafka`
**Created**: 2026-05-24
**Status**: AUDITED
**Syllabus Section**: Level 4: Resilient & Event-Driven Systems

## Syllabus Alignment *(mandatory)*

- **Concept**: Reactor Kafka, non-blocking Producer/Consumer, backpressure-aware message processing.
- **Prerequisites**: LAB-011 (Reactive Web with Spring WebFlux), LAB-009 (Backpressure Strategies & Rate Limiting)
- **Learning Objectives**:
  - LO-001: Understand and implement a non-blocking Kafka Producer using Reactor Kafka.
  - LO-002: Understand and implement a non-blocking Kafka Consumer with backpressure awareness.
  - LO-003: Handle consumer offsets and error scenarios in a reactive messaging pipeline.

## Interactive Scenarios & Validation *(mandatory)*

### Scenario 1 - Non-Blocking Message Production (Priority: P1)

The learner will configure a Reactor Kafka `Sender` and publish a stream of events to a Kafka topic in a fully non-blocking manner.

**Validation (Automated Test)**: A JUnit `StepVerifier` test that consumes the produced messages from the target topic and verifies their content and sequence.

**Acceptance Scenarios**:

1. **Given** a stream of domain events, **When** the learner uses `KafkaSender.send()`, **Then** the events are asynchronously produced to the Kafka topic and confirmed via `SenderResult`.

---

### Scenario 2 - Batching & Backpressure-Aware Consumption (Priority: P1)

The learner will configure a Reactor Kafka `Receiver` to consume messages, controlling ingestion using backpressure and committing offsets in batches using `commitBatchSize` and `commitInterval`.

**Validation (Automated Test)**: A JUnit `StepVerifier` test using a `KafkaReceiver` that verifies the consumer correctly receives messages and uses micro-batching.

---

### Scenario 3 - Error Handling & Dead Letter Queue (DLQ) (Priority: P1)

The learner will implement reactive error handling using `retryWhen` (exponential backoff) and `onErrorResume` to route persistently failing messages to a Dead Letter Queue (DLQ) topic without halting the main stream.

**Validation (Automated Test)**: A JUnit `StepVerifier` test that injects a failing message, verifies it is retried 3 times, and ensures it arrives at the DLQ topic while other messages are successfully processed.

---

### Scenario 4 - Complex Backpressure & Prefetching Tuning (Priority: P2)

The learner will explore how `limitRate` and prefetch configurations influence the Kafka `poll()` behavior when downstream processing is slow.

---

## Educational Requirements *(mandatory)*

### Concepts to Explain

- **EX-001**: Reactor Kafka Architecture vs Spring Kafka (Blocking vs Non-blocking).
- **EX-002**: Reactive Backpressure translation to Kafka consumer pull mechanics (`poll()`).
- **EX-003**: Managing Offset commits reactively (At-most-once vs At-least-once).

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

- **SC-001**: Learner successfully builds a reactive Kafka pipeline capable of producing and consuming messages asynchronously.
- **SC-002**: All validation tests pass upon completion.

## Assumptions

- Learner understands Project Reactor foundations and Spring Boot setup.
- Docker Desktop and Java 21+ are installed.
