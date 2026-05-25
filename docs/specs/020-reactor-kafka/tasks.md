# Tasks: LAB-020: Reactive Messaging with Apache Kafka

**Input**: Design documents from `/specs/020-reactor-kafka/`
**Prerequisites**: plan.md (required), spec.md (required).

**Validation Goal**: This project follows strict "TDD for Learning" (Test-Driven Learning). The automated tests (Jest/JUnit) MUST ensure exactly what the README teaches.

## Phase 1: Monorepo Setup & Data Seeding
- [x] T001 Scaffold lab directory `labs/020-reactor-kafka` by cloning `labs/000-base-setup/`.
- [x] T002 Update root `pom.xml` workspace identifier to include the new lab.
- [x] T003 Ensure `docker-compose.yml` reflects the correct container name format (e.g. `reactive_lab_kafka`). Set up Zookeeper and Kafka images.
- [x] T004 Add `reactor-kafka` dependency to the lab's `pom.xml`.

## Phase 2: Validation Framework Implementation (TDD)
- [x] T005 Write validation script logic (JUnit/Testcontainers) to cover Scenario 1 outcomes (Producer). Use `StepVerifier`, AssertJ, and ensure test classes/methods are package-private.
- [x] T006 Write validation script logic to cover Scenario 2 outcomes (Consumer and offset management). Use `StepVerifier` and ensure no `public` modifiers on tests.
- [x] T007 Provide required codebase comments on what each test step validates.

## Phase 3: Educational Content & Hands-On Environment
- [x] T008 Write the theoretical `CONCEPT.md` detailing the "Why" behind Reactor Kafka and backpressure.
- [x] T009 Write the step-by-step native `README.md` guide for Scenario 1 and 2. MUST include a "Self-Assessment" section with collapsible `<details>` blocks and relative links to source code for all scenarios.
- [x] T010 Inject **Command Dissection** blocks into `README.md` for `docker-compose` Kafka flags and Reactor Kafka operators. Provide `podman-compose` alternatives.

## Phase 4: Idempotency & Clean Up Verifications
- [x] T011 Verify `README.md` Atomic Cleanup command runs natively without errors (`docker-compose down -v --remove-orphans`).
- [x] T012 Verify `Makefile` shortcut targets operate purely as optional proxies.
- [x] T013 Perform end-to-end "Learner Journey" Walkthrough.

## Phase 5: Lab Refiner
- [x] R001 Add `consumeWithStrictBackpressure` to `ReactiveConsumer.java`.
- [x] R002 Add `testScenario4_PrefetchAndBackpressure` to `KafkaTest.java`.
- [x] R003 Add Zookeeper vs KRaft differentiation to `CONCEPT.md`.
- [x] R004 Add Scenario 4 section to `README.md`.
