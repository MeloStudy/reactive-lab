# Tasks: LAB-007: Threading Models & Schedulers

## Phase 1: Infrastructure
- [ ] T001 Initialize Maven `pom.xml` and package structure.
- [ ] T002 Verify project compiles.

## Phase 2: Thread Control Scenarios
- [ ] T003 Implement `BlockingService` (Isolation via `subscribeOn`).
- [ ] T004 Implement `ComputeService` (Offloading via `publishOn`).
- [ ] T005 Test thread name transitions.

## Phase 3: Context Propagation
- [ ] T006 Demonstrate `ThreadLocal` failure during thread hops.
- [ ] T007 Implement Reactor `Context` propagation.
- [ ] T008 Test state persistence across multiple `publishOn` calls.

## Phase 4: Advanced Threading Rules
- [ ] T009 Implement the "Double SubscribeOn" trap demonstration.
- [ ] T010 Implement the "Multi-Hop" execution flow.

## Phase 5: Educational Content
- [ ] T011 Write `CONCEPT.md` (Thread models and Context).
- [ ] T012 Write `README.md` with "Knowledge Check" quiz.
- [ ] T013 Add Command Dissection for `publishOn` vs `subscribeOn`.

## Phase 6: Final Audit
- [ ] T014 Verify all tests pass.
- [ ] T015 Final pedagogical certification.
