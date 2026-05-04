# Implementation Plan: LAB-010: Testing & Debugging Matrix [AUDITED]

**Branch**: `010-testing-debugging-matrix` | **Date**: 2026-05-04
**Input**: Specification from `/specs/010-testing-debugging-matrix/spec.md`

## Summary

This lab focuses on the "Observability" and "Verifiability" of reactive pipelines. The learner will transition from basic testing to advanced techniques that handle time, branching logic, and state propagation, while mastering the tools needed to debug asynchronous stack traces.

## Phase 1: Monorepo Infrastructure
1. **Scaffold**: Clone from `labs/000-base-setup/`.
2. **Workspace Registration**: Add `010-testing-debugging-matrix` as a module in root `pom.xml`.

## Phase 2: Scenario 1 - Testing with Virtual Time
1. Define the instructional path: Use `StepVerifier.withVirtualTime(() -> flux)` and `expectNoEvent(Duration)`.
2. Establish validation: Verify elements emitted over long intervals.

## Phase 3: Scenario 2 - Probing & Context
1. Define the instructional path: Introduce `PublisherProbe` for verifying hidden branches and `ContextView` for state propagation.
2. Establish validation: Assert that a "Logging Branch" was hit using the probe.

## Phase 4: Full Documentation
1. Draft `CONCEPT.md`: Focus on Assembly Time vs Execution Time. Explain the "Lost Stack Trace" problem.
2. Draft `README.md`: Walkthrough of a debugging session.
3. Apply Command Dissection for `withVirtualTime`, `PublisherProbe`, `Hooks.onOperatorDebug()`, and `contextWrite()`.

## Constitution Compliance Check
- [ ] No `.sh` wrapper scripts.
- [ ] Language: English.
- [ ] Dependency Governance: Inherit from parent POM.

## Open Questions
- None. (BlockHound introductory integration confirmed).
