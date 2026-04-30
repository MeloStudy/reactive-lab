# Tasks: LAB-001: The Reactive Manifesto & Asynchronous Paradigms

**Input**: Design documents from `/specs/001-reactive-manifesto/`
**Prerequisites**: plan.md (required), spec.md (required).

**Validation Goal**: This project follows strict "TDD for Learning" (Test-Driven Learning). The automated tests (Jest) MUST ensure exactly what the README teaches.

## Phase 1: Monorepo Setup & Data Seeding
- [x] T001 Scaffold lab directory `labs/001-reactive-manifesto/`.
- [x] T002 Update root `package.json` or project configuration for the new lab.
- [x] T003 Ensure `docker-compose.yml` (if needed for mock services) is configured.
- [x] T004 Implement simulated "Brittle Service" for Scenario 1.

## Phase 2: Validation Framework Implementation (TDD)
- [x] T005 Write Jest tests for Scenario 1 (Manifesto Pillars validation).
- [x] T006 Write Jest tests for Scenario 2 (Observable vs Promise signals).
- [x] T007 Provide required codebase comments on what each test step validates.

## Phase 3: Educational Content & Hands-On Environment
- [x] T008 Write the theoretical `CONCEPT.md` detailing the "Why" and the Manifesto.
- [x] T009 Write the step-by-step native `README.md` guide for Scenario 1 and 2.
- [x] T010 Inject **Command Dissection** blocks for `Observable`, `subscribe`, and `npm test`.

## Phase 4: Idempotency & Clean Up Verifications
- [x] T011 Verify `README.md` Atomic Cleanup command runs natively.
- [x] T012 Verify any Makefile targets operate as optional proxies.
- [x] T013 Perform end-to-end "Learner Journey" Walkthrough.
