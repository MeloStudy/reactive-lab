# Tasks: LAB-006: Combining & Basic Error Handling

## Phase 1: Infrastructure
- [ ] T001 Initialize Maven `pom.xml` and package structure.
- [ ] T002 Verify project compiles.

## Phase 2: Stream Combination
- [ ] T003 Implement `DashboardService` using `zip`.
- [ ] T004 Implement `ActivityAggregator` using `merge` and `concat`.
- [ ] T005 Test combination scenarios (order and interleaving).

## Phase 3: Error Handling Strategies
- [ ] T006 Implement `ResilientClient` with `doOnError` and `onErrorReturn`.
- [ ] T007 Implement exception translation with `onErrorMap`.
- [ ] T008 Implement failover logic with `onErrorResume`.

## Phase 4: Resilience Foundations
- [ ] T009 Implement transient error recovery with `retry`.
- [ ] T010 Verify retry count using `PublisherProbe`.

## Phase 5: Educational Content
- [ ] T011 Write `CONCEPT.md` (Marble diagrams and Error Channel).
- [ ] T012 Write `README.md` with "Knowledge Check" quiz.
- [ ] T013 Add Command Dissection for `zip` and `onErrorResume`.

## Phase 6: Final Audit
- [ ] T014 Verify all tests pass.
- [ ] T015 Final pedagogical certification.
