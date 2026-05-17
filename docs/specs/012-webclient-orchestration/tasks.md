# Tasks: LAB-012: WebClient: Orchestrating Downstream Services [AUDITED]

**Input**: Design documents from `docs/specs/012-webclient-orchestration/`
**Prerequisites**: plan.md, spec.md.

**Validation Goal**: Implement a robust suite of unit tests using `MockWebServer` and `WebTestClient` that guide the learner through building a non-blocking orchestrator.

## Phase 1: Infrastructure & Project Setup
- [x] T001 Create `labs/012-webclient-orchestration` directory and standard Maven structure.
- [x] T002 Update root `pom.xml` to include `012-webclient-orchestration` as a module.
- [x] T003 Configure `pom.xml` dependencies (`spring-boot-starter-webflux`, `mockwebserver`, `lombok`).

## Phase 2: Scenario Implementation (TDD)
- [x] T004 Implement Scenario 1 test: Simple GET call to `MockWebServer`.
- [x] T005 Implement Scenario 2 test: Parallel calls with `Flux.zip`.
- [x] T006 Implement Scenario 3 test: Dependent calls with `flatMap`.
- [x] T007 Implement Scenario 4 test: Timeout and Retry logic validation.
- [x] T008 Implement Scenario 5 test: Streaming response consumption.

## Phase 3: Documentation & Educational Content
- [x] T009 Create `CONCEPT.md` focusing on `WebClient` vs `RestTemplate` and the Netty event loop.
- [x] T010 Create `README.md` with step-by-step instructions and Command Dissections for `onStatus`, `retrieve`, and `zip`.
- [x] T011 Add code comments explaining the "Reactive" reason behind every test assertion.

## Phase 4: Refinement & Constitution Compliance
- [x] T015 Integrate **BlockHound** into `ReactiveOrchestratorTest`.
- [x] T016 Implement Scenario 6 (exchangeToMono) and its validation.
- [x] T017 Deepen `CONCEPT.md` with Netty Event Loop and Memory safety details.
- [x] T018 Add "Troubleshooting" and "Native Execution" to `README.md`.

## Phase 5: Final Certification
- [x] T012 Verify all tests pass with `mvn test`.
- [x] T013 Verify compliance with the Constitution (No `.sh` files, English only, valid metadata).
- [x] T014 Update `docs/syllabus.md` status to `AUDITED`.

## Phase 6: Constitution v0.2.7 Refinement
- [x] T019 Update `spec.md` and `plan.md` with Virtual Threads and Traceable Implementation goals.
- [x] T020 Add Virtual Threads (Project Loom) conceptual comparison to `CONCEPT.md`.
- [x] T021 Add Traceable Implementation relative links to all scenarios in `README.md`.
- [x] T022 Execute `mvn test` to ensure stability post-refinement.

## Phase 7: Constitution v0.2.8 Refinement (Retrospective Upgrade)
- [x] T023 Expand `spec.md` and `plan.md` to target Constitution v0.2.8 upgrades.
- [x] T024 Update `CONCEPT.md` with Netty Selector Event Loop Mermaid diagram and deep-dive Event Loop thread delegation vs Thread-per-Request.
- [x] T025 Enhance `CONCEPT.md` with Netty Connection Pool metrics and Loom thread pinning analysis.
- [x] T026 Refactor `ReactiveOrchestratorTest.java` to add deep inline educational comments for all StepVerifier assertions.
- [x] T027 Standardize AssertJ check statements to follow fluent dedicated paradigms.
- [x] T028 Run `mvn test` to verify full system stability post-upgrade.



