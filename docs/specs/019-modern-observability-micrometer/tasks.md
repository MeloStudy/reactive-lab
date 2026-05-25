# Tasks: LAB-019: Modern Observability (Micrometer Observation)

**Input**: Design documents from `/specs/019-modern-observability-micrometer/`
**Prerequisites**: plan.md (required), spec.md (required).

**Validation Goal**: This project follows strict "TDD for Learning" (Test-Driven Learning). The automated tests (JUnit) MUST ensure exactly what the README teaches.

## Phase 1: Monorepo Setup & Data Seeding
- [x] T001 Scaffold lab directory by cloning `labs/000-base-setup/`.
- [x] T002 Update local `pom.xml` workspace identifier.
- [x] T003 Ensure `docker-compose.yml` reflects the correct container name format (e.g. `reactive_lab_019`).
- [x] T004 Implement dummy downstream service or mock for WebClient interaction.
- [x] T004b Configure Prometheus and Zipkin in `docker-compose.yml` for visualization.

## Phase 2: Validation Framework Implementation (TDD)
- [x] T005 Write validation script logic using `TestObservationRegistry` to cover Scenario 1 outcomes (automatic WebFlux/WebClient tracing).
- [x] T006 Write validation script logic to cover Scenario 2 outcomes (custom observations and high/low cardinality keys).
- [x] T007 Provide required codebase comments on what each test step validates.

## Phase 3: Educational Content & Hands-On Environment
- [x] T008 Write the theoretical `CONCEPT.md` detailing Micrometer Observation and Context Propagation.
- [x] T009 Write the step-by-step native `README.md` guide for Scenario 1 and 2.
- [x] T010 Inject **Command Dissection** blocks into `README.md` for any new configuration or dependencies.

## Phase 4: Idempotency & Clean Up Verifications
- [x] T011 Verify `README.md` Atomic Cleanup command runs natively without errors (`docker-compose down -v --remove-orphans`).
- [x] T012 Verify `Makefile` shortcut targets operate purely as optional proxies.
- [x] T013 Perform end-to-end "Learner Journey" Walkthrough.

## Phase 5: Lab Refiner
- [x] R001 Update `docker-compose.yml` to use secure, latest recommended image tags for Prometheus and Zipkin.
- [x] R002 Refactor `ObservabilityService.java` to use explicit `Observation` API and add high cardinality tags.
- [x] R003 Update `Scenario2Test.java` to validate the new high cardinality tag.
- [x] R004 Update `README.md` to include relative links to source code, `podman` alternatives, and Command Dissections for Maven.
