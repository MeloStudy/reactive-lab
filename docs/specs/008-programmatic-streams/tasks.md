# Tasks: LAB-008: Programmatic Stream Generation

## Phase 1: Infrastructure
- [ ] T001 Initialize Maven `pom.xml` and package structure.
- [ ] T002 Verify project compiles.

## Phase 2: Pull-Based Generation
- [ ] T003 Implement Fibonacci generator using `Flux.generate`.
- [ ] T004 Test stateful sequence generation.

## Phase 3: Push-Based Bridging
- [ ] T005 Implement `ChatBridge` using `Flux.create`.
- [ ] T006 Implement resource cleanup via `onDispose`.
- [ ] T007 Implement the Overflow Scenario (`LATEST`/`DROP`).

## Phase 4: Standalone Publishers (Sinks)
- [ ] T008 Implement `NotificationBus` using `Sinks.Many`.
- [ ] T009 Implement `OneShotSignal` using `Sinks.One`.
- [ ] T010 Test multi-subscriber support (Multicast).

## Phase 5: Educational Content
- [ ] T011 Write `CONCEPT.md` (Push vs Pull and Sink types).
- [ ] T012 Write `README.md` with "Knowledge Check" quiz.
- [ ] T013 Add Command Dissection for `Sinks` and `Flux.create`.

## Phase 6: Final Audit
- [ ] T014 Verify all tests pass.
- [ ] T015 Final pedagogical certification.
