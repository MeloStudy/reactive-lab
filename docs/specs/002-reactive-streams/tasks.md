# Tasks: LAB-002: The Reactive Streams Specification & TCK

**Validation Goal**: Ensure the learner can implement a spec-compliant Publisher and validate it using the official TCK.

## Phase 1: Java Scaffolding & Dependencies
- [x] T001 Initialize `pom.xml` with Java 21 and Reactive Streams dependencies.
- [x] T002 Verify project compiles via `mvn compile`.
- [x] T003 Scaffold base package `com.reactivelab.streams`.

## Phase 1: Rule Mapping & Documentation
- [x] R001 Document `CustomPublisher.java` with explicit Rule references [CustomPublisher.java](file:///d:/repos/reactive-lab/labs/002-reactive-streams/src/main/java/com/reactivelab/streams/CustomPublisher.java)
- [x] R002 Document `CustomSubscriber.java` with Subscriber-specific Rules [CustomSubscriber.java](file:///d:/repos/reactive-lab/labs/002-reactive-streams/src/main/java/com/reactivelab/streams/CustomSubscriber.java)

## Phase 2: TCK & Test Alignment
- [x] R003 Implement `createFailedPublisher` in `PublisherTCKTest.java` [PublisherTCKTest.java](file:///d:/repos/reactive-lab/labs/002-reactive-streams/src/test/java/com/reactivelab/streams/PublisherTCKTest.java)
- [x] R004 Validate that the reference solution passes `mvn test`.

## Phase 3: Documentation & Audit
- [x] R005 Update `README.md` with TCK Failure Guide and Rule Mapping [README.md](file:///d:/repos/reactive-lab/labs/002-reactive-streams/README.md)
- [x] R006 Final status update to `AUDITED` in Spec and Syllabus
- [x] R007 Perform final gap audit against Constitution Article IVerify all TCK tests pass (or a specific subset required for LOs).
- [x] T014 Perform final audit for "Pedagogical Clarity".

## Phase 6: Refinement & Rigor Upgrade
- [x] T015 Move `CustomSubscriber` to `src/main/java` (Core LO).
- [x] T016 Deepen `CONCEPT.md` with official specification rules and TCK architecture.
- [x] T017 Add "Reference Documentation" and "TCK Failure Analysis" to `README.md`.
- [x] T018 Verify that all instructions use the correct lab slug (`002-reactive-streams`).
- [x] T019 Re-run full test suite and TCK to ensure package move didn't break anything.
