# Tasks: [LAB NAME]

**Input**: Design documents from `/specs/[###-lab-name]/`
**Prerequisites**: plan.md (required), educational-spec.md (required).

**Validation Goal**: This project follows strict "TDD for Learning" (Test-Driven Learning). The automated tests (Jest/JUnit) MUST ensure exactly what the README teaches.

## Phase 1: Monorepo Setup & Data Seeding
- [ ] T001 Scaffold lab directory by cloning `labs/000-base-setup/`.
- [ ] T002 Update local `package.json` workspace identifier (or `pom.xml` if Java Module).
- [ ] T003 Ensure `docker-compose.yml` reflects the correct container name format (e.g. `reactive_lab_XXX`).
- [ ] T004 Implement event stream seeding or broker configuration if the lab requires pre-existing data.

## Phase 2: Validation Framework Implementation (TDD)
- [ ] T005 Write validation script logic (e.g., `tests/validator.test.js`) to cover Scenario 1 outcomes.
- [ ] T006 Write validation script logic to cover Scenario 2 outcomes.
- [ ] T007 Provide required codebase comments on what each test step validates.

## Phase 3: Educational Content & Hands-On Environment
- [ ] T008 Write the theoretical `CONCEPT.md` detailing the "Why".
- [ ] T009 Write the step-by-step native `README.md` guide for Scenario 1 and 2.
- [ ] T010 Inject **Command Dissection** blocks into `README.md` for any new native CLI flags, Reactive Operators, or Threading Configurations.

## Phase 4: Idempotency & Clean Up Verifications
- [ ] T011 Verify `README.md` Atomic Cleanup command runs natively without errors (`docker-compose down -v --remove-orphans`).
- [ ] T012 Verify `Makefile` shortcut targets operate purely as optional proxies.
- [ ] T013 Perform end-to-end "Learner Journey" Walkthrough.
