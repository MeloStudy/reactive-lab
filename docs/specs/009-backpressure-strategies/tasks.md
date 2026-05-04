# Tasks: LAB-009: Backpressure Strategies & Flow Control

**Input**: Design documents from `/specs/009-backpressure-strategies/`
**Prerequisites**: plan.md (required), spec.md (required).

**Validation Goal**: This project follows strict "TDD for Learning" (Test-Driven Learning). The automated tests (JUnit/StepVerifier) MUST ensure exactly what the README teaches.

## Phase 1: Monorepo Setup & Data Seeding
- [ ] T001 Scaffold lab directory by cloning `labs/000-base-setup/`.
- [ ] T002 Update root `pom.xml` to include `009-backpressure-strategies`.
- [ ] T003 Ensure `docker-compose.yml` reflects the correct container name format `reactive_lab_009`.
- [ ] T004 Implement a "Fast Producer" helper class using `Sinks.many().multicast()` to generate signals that bypass standard backpressure.

## Phase 2: Validation Framework Implementation (TDD)
- [ ] T005 Write JUnit tests for Scenario 1 (Buffering/Error) using `StepVerifier`.
- [ ] T006 Write JUnit tests for Scenario 2 (Dropping/Latest/Windowing).
- [ ] T007 Provide required codebase comments on what each test step validates.

## Phase 3: Educational Content & Hands-On Environment
- [ ] T008 Write the theoretical `CONCEPT.md` detailing the "Pull vs Push" mechanics.
- [ ] T009 Write the step-by-step native `README.md` guide for Scenario 1 and 2.
- [ ] T010 Inject **Command Dissection** blocks into `README.md` for `onBackpressure*` and `window()` operators.

## Phase 4: Idempotency & Clean Up Verifications
- [ ] T011 Verify `README.md` Atomic Cleanup command runs natively without errors.
- [ ] T012 Verify `Makefile` shortcut targets operate purely as optional proxies.
- [ ] T013 Perform end-to-end "Learner Journey" Walkthrough.
