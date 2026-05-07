# Tasks: LAB-008: Programmatic Stream Generation & Hot/Cold

## Phase 1: Infrastructure
- [x] T001 Initialize Maven `pom.xml` and package structure.

## Phase 2: Generation & Bridging
- [x] T003 Implement Fibonacci generator (`Flux.generate`).
- [x] T005 Implement `ChatBridge` (`Flux.create`).

## Phase 3: Hot vs Cold Mechanics
- [x] T006 Implement `Broadcaster` scenario.
- [x] T007 Implement `OnDemandResource` (`refCount`).
- [x] T008 Write tests for Hot vs Cold comparison using VirtualTime.

## Phase 4: Sinks & Caching
- [x] T009 Implement `NotificationBus` (`Sinks.Many`).
- [x] T010 Implement `ResultCache` (`cache`).

## Phase 5: Refinement (Engineering Rigor)
- [x] T011 Expand `CONCEPT.md` with deep pull/push mechanics and Sinks safety.
- [x] T012 Add `@Slf4j` and logging to all implementation classes.
- [x] T013 Update `README.md` with `cache()` dissection and advanced quiz.
- [x] T014 Refine `OnDemandResourceTest` for lifecycle validation.

## Phase 6: Final Audit
- [x] T015 Verify all tests pass (`mvn test`).
- [x] T016 Set status to `AUDITED` in `spec.md`.
