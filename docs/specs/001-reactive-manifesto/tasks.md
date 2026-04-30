# Tasks: LAB-001: The Reactive Manifesto & Asynchronous Paradigms

**Input**: Design documents from `/specs/001-reactive-manifesto/`
**Prerequisites**: plan.md (required), spec.md (required).

**Validation Goal**: This project follows strict "TDD for Learning" (Test-Driven Learning). The automated tests (Jest) MUST ensure exactly what the README teaches.

## Phase 1: Setup & Manual Observability
- [x] T001 Scaffold `src/scenarios/` directory.
- [x] T002 Implement `src/scenarios/manifesto.js` (Manual Runner for Scenario 1).
- [x] T003 Implement `src/scenarios/paradigms.js` (Manual Runner for Scenario 2).
- [x] T004 Implement `src/scenarios/elasticity.js` (Manual Runner for Scenario 3).

## Phase 2: Validation Framework Upgrade (TDD)
- [x] T005 Refactor `tests/scenario-2.test.js` to use `rxjs/testing` TestScheduler.
- [x] T006 Write Jest tests for Scenario 3 (Elasticity validation).
- [x] T007 Ensure all tests provide clear comments on validation logic.

## Phase 3: Educational Deep-Dive (High Rigor)
- [x] T008 Rewrite `CONCEPT.md` with Event Loop details and Mermaid diagrams.
- [x] T009 Update `README.md` to emphasize the "Manual Execution" first path.
- [x] T010 Add **Command Dissection** for new concepts/operators introduced.

## Phase 4: Final Certification
- [x] T011 Verify all manual scripts run with `node src/scenarios/XXX.js`.
- [x] T012 Verify all automated tests pass with `npm test`.
- [x] T013 Perform final "Learner Journey" audit.
