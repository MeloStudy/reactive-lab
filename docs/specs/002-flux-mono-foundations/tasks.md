# Tasks: LAB-002: Flux & Mono Foundations

**Input**: Design documents from `/docs/specs/002-flux-mono-foundations/`
**Prerequisites**: plan.md, spec.md.

**Validation Goal**: Ensure the learner can distinguish between assembly and execution and correctly use the fluent API of Project Reactor.

## Phase 1: Project Setup
- [x] T001 Initialize Maven `pom.xml` with Reactor Core and Reactor Test dependencies.
- [x] T002 Create the package structure `com.reactivelab.foundations`.
- [x] T003 Setup a base `LabResult` class or simple logger to facilitate side-effect verification.

## Phase 2: Scenario 1 - Lazy Execution
- [x] T004 Implement `LazyGreeter.java` with a method returning a `Mono` that has side effects.
- [x] T005 Write `LazyGreeterTest.java` using `StepVerifier` to prove side effects only occur on subscription.

## Phase 3: Scenario 2 - Immutability
- [x] T006 Implement `ImmutablePipeline.java` with a source `Flux`.
- [x] T007 Write `ImmutablePipelineTest.java` verifying that `flux.map()` does not mutate the original instance.

## Phase 4: Factory Methods
- [x] T008 Implement `StreamFactories.java` demonstrating `fromIterable`, `fromCallable`, and `error`.
- [x] T009 Write `StreamFactoriesTest.java` with comprehensive `StepVerifier` signal checks.

## Phase 5: Educational Content
- [x] T010 Write `CONCEPT.md` focusing on "Assembly vs Subscription".
- [x] T011 Write `README.md` with step-by-step instructions and command dissections.
- [x] T012 Add "Command Dissection" for `Flux.just`, `Mono.fromCallable`, and `StepVerifier.create()`.

## Phase 6: Final Audit
- [x] T013 Verify all tests pass with `mvn test`.
- [x] T014 Ensure compliance with the Reactive Lab Constitution.
- [x] T015 Perform the "Learner Journey" walkthrough.
