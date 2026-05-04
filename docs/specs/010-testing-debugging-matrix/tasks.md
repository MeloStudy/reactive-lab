# Tasks: LAB-010: Testing & Debugging Matrix

**Input**: Design documents from `/specs/010-testing-debugging-matrix/`
**Prerequisites**: plan.md, spec.md.

## Phase 1: Monorepo Setup & Data Seeding
- [ ] T001 Scaffold lab directory by cloning `labs/000-base-setup/`.
- [ ] T002 Update root `pom.xml` to include `010-testing-debugging-matrix`.
- [ ] T003 Ensure `docker-compose.yml` reflects `reactive_lab_010`.

## Phase 2: Validation Framework Implementation (TDD)
- [ ] T004 Write JUnit tests for Scenario 1 (Virtual Time) using `StepVerifier.withVirtualTime`.
- [ ] T005 Write JUnit tests for Scenario 2 (Probing) using `PublisherProbe`.
- [ ] T006 Write JUnit tests for Scenario 3 (Context) verifying state propagation.
- [ ] T007 Write JUnit tests for Scenario 4 (BlockHound) expecting `BlockingOperationError`.

## Phase 3: Educational Content & Hands-On Environment
- [ ] T007 Write `CONCEPT.md` on Assembly vs Execution time.
- [ ] T008 Write `README.md` guide with "Command Dissection".
- [ ] T009 Create a "Buggy Pipeline" class for the learner to fix using debugging tools.

## Phase 4: Idempotency & Clean Up
- [ ] T010 Verify native cleanup.
- [ ] T011 Perform "Learner Journey" Walkthrough.
