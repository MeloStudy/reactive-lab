# Tasks: LAB-004: Subscriptions & Lifecycle Control

**Input**: Design documents from `/docs/specs/004-subscriptions-lifecycle/`
**Prerequisites**: plan.md, spec.md.

## Phase 1: Project Setup
- [ ] T001 Initialize Maven `pom.xml` with Reactor dependencies.
- [ ] T002 Create the package structure `com.reactivelab.lifecycle`.

## Phase 2: Scenario 1 - Disposable
- [ ] T003 Implement `FirehoseManager.java` returning a `Disposable`.
- [ ] T004 Write `FirehoseManagerTest.java` using virtual time to verify cancellation.

## Phase 3: Scenario 2 - CompositeDisposable
- [ ] T005 Implement `SubscriptionGroup.java` managing multiple `Disposable`.
- [ ] T006 Write `SubscriptionGroupTest.java` verifying bulk cancellation.

## Phase 4: Scenario 3 - BaseSubscriber
- [ ] T007 Implement `SmartSubscriber.java` extending `BaseSubscriber`.
- [ ] T008 Write `SmartSubscriberTest.java` verifying manual request flow.

## Phase 5: Scenario 4 - Automatic Lifecycle
- [ ] T009 Implement `LifecycleComparison.java` comparing `dispose()` vs `take()`.
- [ ] T010 Write `LifecycleComparisonTest.java` verifying automatic cancel signals.

## Phase 6: Scenario 5 - Lifecycle Hooks
- [ ] T011 Implement `LifecycleTracker.java` with `doOn...` operators.
- [ ] T012 Write `LifecycleTrackerTest.java` verifying hook execution order.


## Phase 5: Educational Content
- [ ] T009 Write `CONCEPT.md` focusing on the Subscription Contract.
- [ ] T010 Write `README.md` with step-by-step instructions.
- [ ] T011 Add "Command Dissection" for `subscribe()`, `dispose()`, and `BaseSubscriber`.
- [ ] T012 Add **Interactive Self-Assessment** (Quiz) to README.md.

## Phase 6: Final Audit
- [ ] T012 Verify all tests pass with `mvn test`.
- [ ] T013 Ensure compliance with the Reactive Lab Constitution.
- [ ] T014 Perform the "Learner Journey" walkthrough.
