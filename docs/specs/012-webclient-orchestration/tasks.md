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

