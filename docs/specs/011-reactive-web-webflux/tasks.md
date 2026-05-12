# Tasks: LAB-011: Reactive Web with Spring WebFlux [AUDITED]

**Input**: Design documents from `/specs/011-reactive-web-webflux/`
**Prerequisites**: plan.md, spec.md.

## Phase 1: Structural Migration
- [x] T001 Create sub-packages: `controller`, `handler`, `router`, `service`, `model`, `exception`.
- [x] T002 Move Java files to their respective packages.
- [x] T003 Update module in root `pom.xml` (inherited).

## Phase 2: Business Logic & Refinement
- [x] T004 Apply `@Slf4j` to all components for pedagogical logging.
- [x] T005 Implement architectural restructuring of classes.
- [x] T006 Update imports and package declarations.

## Phase 3: Documentation & Educational Content
- [x] T007 Update `spec.md` to `AUDITED` with revised LOs.
- [x] T008 Update `CONCEPT.md` with Backpressure and Modern JVM Context.
- [x] T009 Update `README.md` with interactive Knowledge Check.

## Phase 4: Validation
- [x] T010 Remove `public` modifiers from all test classes (Rule VI.30).
- [x] T011 Verify "Learner Journey" via `mvn clean test`.
