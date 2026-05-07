# Tasks: LAB-004: Subscriptions, Lifecycle & Side Effects

**Input**: Design documents from `/docs/specs/004-subscriptions-lifecycle/`
**Prerequisites**: plan.md, spec.md.

## Phase 1: Project Setup
- [x] T001 Initialize Maven `pom.xml` with Reactor dependencies.
- [x] T002 Create the package structure `com.reactivelab.lifecycle`.

## Phase 2: Scenario 1 - Disposable (Manual Cancellation)
- [x] T003 Implement `FirehoseManager.java` returning a `Disposable`.
- [x] T004 Write `FirehoseManagerTest.java` using virtual time to verify cancellation.

## Phase 3: Scenario 2 - Grouped Cancellation
- [x] T005 Implement `SubscriptionGroup.java` managing multiple `Disposable`.
- [x] T006 Write `SubscriptionGroupTest.java` verifying bulk cancellation.

## Phase 4: Scenario 3 - BaseSubscriber (Backpressure)
- [x] T007 Implement `SmartSubscriber.java` extending `BaseSubscriber`.
- [x] T008 Write `SmartSubscriberTest.java` verifying manual request flow.

## Phase 5: Scenario 4 - Automatic Lifecycle
- [x] T009 Implement `LifecycleComparison.java` comparing `dispose()` vs `take()`.
- [x] T010 Write `LifecycleComparisonTest.java` verifying automatic cancel signals.

## Phase 6: Signal Peekers (Side Effects) [REFINED]
- [x] T011 Implement `LifecycleTracker.java` to include `doOn...` and `doFinally`.
- [x] T012 Write `LifecycleTrackerTest.java` verifying hook execution order.

## Phase 7: Educational Content [REFINED]
- [x] T013 Refine `CONCEPT.md` with Subscription State Machine deep-dive.
- [x] T014 Update `README.md` with standardized LifecycleTracker documentation.
- [x] T015 Enhance **Interactive Self-Assessment** with lifecycle questions.

## Phase 8: Final Audit
- [ ] T016 Verify all tests pass with `mvn test`.
- [ ] T017 Ensure compliance with the Reactive Lab Constitution.
- [ ] T018 Perform the "Learner Journey" walkthrough.
