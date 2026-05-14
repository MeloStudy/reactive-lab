# Implementation Plan: LAB-010: Testing & Debugging Matrix

**Branch**: `010-testing-debugging-matrix` | **Date**: 2026-05-04
**Input**: Specification from `/specs/010-testing-debugging-matrix/spec.md`

## Phase 1: Project Setup
1. Create `labs/010-testing-debugging-matrix`.
2. Add **BlockHound** to `pom.xml`.
3. Package: `com.reactivelab.testing`.

## Phase 2: Mastering Time & Branches
1. **Scenario 1 (Virtual Time)**: Implement `LongRunningProcess`.
2. **Scenario 2 (PublisherProbe)**: Implement `FallbackOrchestrator`.
3. **Test**: Warp a 1-year stream and verify fallback branch subscriptions.

## Phase 3: Context & Debugging (New)
1. **Scenario 3 (Context)**: Implement `ContextualTracer`.
2. **Scenario 5 (Checkpoint)**: Implement `ErrorPronePipeline`.
3. **Test**: Propagate Trace IDs and identify errors via labels.

## Phase 4: Enforcing Non-Blocking (New)
1. **Scenario 4 (BlockHound)**: Implement `BlockHoundDetector`.
2. **Test**: Install BlockHound and verify it catches a `Thread.sleep` in a reactive flatMap.

## Phase 5: Documentation & Assessment
1. **CONCEPT.md**: Explain the "Assembly vs Execution" mental model.
2. **README.md**: 
   - Command Dissection for `withVirtualTime`, `PublisherProbe`, and `Hooks`.
   - Updated **Knowledge Check** section.

## Phase 6: Refinement (Constitution Audit v0.2.7)
1. **JUnit 5**: Remove `public` from test classes.
2. **Pedagogy**: Expand `CONCEPT.md` with Virtual Threads context.
3. **Traceability**: Add root-relative links to `README.md`.
4. **Scannable**: Implement Scenario 6 in tests and documentation.

## Constitution Compliance Check
- [x] Java 21+ syntax.
- [x] `StepVerifier` for everything.
- [x] BlockHound integration included.
- [x] Relative links for traceability.
