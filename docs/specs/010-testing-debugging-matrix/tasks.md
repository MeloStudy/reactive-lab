# Tasks: LAB-010: Testing & Debugging Matrix

## Phase 1: Mastery of Time & Probes
- [x] T001 Implement `LongRunningProcess` for Virtual Time.
- [x] T002 Implement `FallbackOrchestrator` for `PublisherProbe`.
- [x] T003 Refactor tests to use `StepVerifier.withVirtualTime(() -> ...)` (Idiomatic Rule).
- [x] T004 Verify fallback branch subscription without data emission.

## Phase 2: State & Diagnostics
- [x] T005 Implement `ContextualTracer` for Trace ID propagation.
- [x] T006 Implement Scenario 5 (Labeled Checkpoints) in `BuggyService`.
- [x] T007 Verify Context persistence across `publishOn` thread hops.
- [x] T008 Verify stack trace labeling using `checkpoint("label")`.

## Phase 3: Non-Blocking Enforcement
- [x] T009 Install **BlockHound** in the test suite.
- [x] T010 Implement `blockingPipeline` to simulate Event Loop freezes.
- [x] T011 Assert `BlockingOperationError` when `Thread.sleep` is called.

## Phase 4: Documentation & Certification
- [x] T012 Expand `CONCEPT.md` with Assembly vs Execution mental model.
- [x] T013 Add 5-question Self-Assessment to `README.md`.
- [x] T014 Standardize with `@Slf4j` and Lombok.
- [x] T015 Verify all tests pass (`mvn test`).
- [x] T016 Set status to `AUDITED` in `spec.md`.
