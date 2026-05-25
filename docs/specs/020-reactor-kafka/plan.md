# Implementation Plan: 020 Reactive Messaging with Apache Kafka

**Branch**: `020-reactor-kafka` | **Date**: 2026-05-24
**Input**: Specification from `/specs/020-reactor-kafka/spec.md`

## Summary

This lab introduces Reactor Kafka, allowing learners to build highly scalable, non-blocking event-driven pipelines. Learners will implement a reactive Kafka producer to publish streams of data, and a reactive Kafka consumer that handles messages with proper backpressure and offset management.

## Phase 1: Monorepo Infrastructure (Base Setup Cloning)
1. **Scaffold**: Clone the base setup from `labs/000-base-setup/`.
2. **Workspace Registration**: Add the new lab as a `<module>` in the root `pom.xml`.
3. **Data Requirements**: The lab requires a running Kafka broker (via Docker Compose). No pre-existing data is needed, as Scenario 1 will produce the data.

## Phase 2: Scenario 1 - Non-Blocking Message Production
1. Configure a Spring Boot application with Reactor Kafka dependencies.
2. Implement a `ReactiveProducer` component using `KafkaSender` to transform an incoming `Flux` of domain objects into `SenderRecord`s and publish them.
3. Establish a JUnit test with an embedded Kafka or Testcontainers to verify that messages published by `ReactiveProducer` reach the topic.

## Phase 3: Scenario 2 - Backpressure-Aware Message Consumption
1. Implement a `ReactiveConsumer` component using `KafkaReceiver` to ingest messages from a topic as a `Flux<ReceiverRecord>`.
2. Teach offset management by explicitly calling `record.receiverOffset().acknowledge()` or using `commitBatch()`.
3. Establish a JUnit test with StepVerifier to publish test messages and verify the `ReactiveConsumer` processes them correctly.

## Phase 4: Full Documentation & Dissection
1. Draft the `CONCEPT.md` ensuring a rigorous technical deep-dive into the concepts of Reactor Kafka, how backpressure maps to Kafka's `poll()` loop, and offset commit strategies.
2. Draft the `README.md` as an educational walkthrough. Enforce strict **Native Execution** logic (`docker-compose up -d`), but ensure each step explains the rationale before the command and analyzes the result after it.
3. Apply the **Command Dissection** pattern inside the `README.md` for ANY new CLI flag or Reactive operator introduced.

## Constitution Compliance Check
- [ ] No `.sh` wrapper scripts abstract the orchestration.
- [ ] Code comments explicitly describe what every test line validates.
- [ ] Language used across all text is explicitly English.
- [ ] **Dependency Governance**: The module correctly inherits from the parent POM and defines NO redundant versions.

## Open Questions
- None. Decided to use **Testcontainers** for the Kafka validation tests as it provides closer-to-production fidelity compared to embedded brokers.
