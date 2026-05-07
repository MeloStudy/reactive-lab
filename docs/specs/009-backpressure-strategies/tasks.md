# Tasks: LAB-009: Backpressure Strategies & Rate Limiting

## Phase 1: Overflow Strategies
- [x] T001 Implement `TelemetryService` with `onBackpressureBuffer`.
- [x] T002 Implement `SensorMonitor` with `onBackpressureLatest`.
- [x] T003 Reproduce and verify `OverflowException`.
- [x] T004 Add `DROP_OLDEST` strategy validation.

## Phase 2: Rate & Request Limiting
- [x] T005 Implement `ThrottledRequester` (`limitRate`).
- [x] T006 Implement `QuotaEnforcer` (`limitRequest`).
- [x] T007 Use `PublisherProbe` to verify exact request signals.
- [x] T008 Add `ReplenishmentTest` for the 75% threshold rule.

## Phase 3: Documentation & Assessment
- [x] T009 Expand `CONCEPT.md` with demand-propagation and prefetch math.
- [x] T010 Fix `README.md` structure and add Command Dissections.
- [x] T011 Add 5-question Self-Assessment to `README.md`.

## Phase 4: Final Certification
- [x] T012 Standardize with `@Slf4j` and Lombok.
- [x] T013 Verify all tests pass (`mvn test`).
- [x] T014 Set status to `AUDITED` in `spec.md`.
