# Tasks: LAB-006: The Reactive Streams Specification & TCK

**Validation Goal**: Ensure the learner can implement a spec-compliant Publisher and validate it using the official TCK.

## Phase 1: Java Scaffolding & Dependencies
- [x] T001 Initialize `pom.xml` with Java 21 and Reactive Streams dependencies.
- [x] T002 Verify project compiles via `mvn compile`.
- [x] T003 Scaffold base package `com.reactivelab.spec`.

## Phase 2: Implementation of the "Raw" Contract
- [x] T004 Implement `CustomPublisher` and its inner `Subscription`.
- [x] T005 Implement `CustomSubscriber` for manual testing.
- [x] T006 Implement Scenario 1: Handshake validation test (JUnit).

## Phase 3: The TCK Rigor
- [x] T007 Integrate the Reactive Streams TCK harness.
- [x] T008 Implement `PublisherTCKTest` to validate `CustomPublisher`.
- [x] T009 Fix common compliance issues identified by the TCK (Learning loop).

## Phase 4: Educational Polish
- [x] T010 Write `CONCEPT.md` with interface diagrams.
- [x] T011 Write `README.md` with Maven execution instructions.
- [x] T012 Add Command Dissection for TCK failure outputs.

## Phase 5: Final Verification
- [x] T013 Verify all TCK tests pass (or a specific subset required for LOs).
- [x] T014 Perform final audit for "Pedagogical Clarity".
