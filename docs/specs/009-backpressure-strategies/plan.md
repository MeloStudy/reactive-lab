# Implementation Plan: LAB-009: Backpressure Strategies & Rate Limiting

**Branch**: `009-backpressure-strategies` | **Date**: 2026-05-04
**Input**: Specification from `/specs/009-backpressure-strategies/spec.md`

## Phase 1: Project Setup
1. Create `labs/009-backpressure-strategies`.
2. Package: `com.reactivelab.flowcontrol`.

## Phase 2: Overflow Strategies
1. **Scenario 1 (Buffer)**: Implement `TelemetryService`. Use `onBackpressureBuffer(100)`.
2. **Scenario 2 (Drop/Latest)**: Implement `SensorMonitor`. Use `onBackpressureLatest()`.
3. **Test**: Reproduce `OverflowException` and verify fix. Use `delayElements` to simulate slow consumption.

## Phase 3: Rate & Request Limiting (New)
1. **Scenario 3 (limitRate)**: Implement `ThrottledRequester`.
2. **Scenario 4 (limitRequest)**: Implement `QuotaEnforcer`.
3. **Test**: Use `PublisherProbe` to assert the exact `request(n)` signals sent to the upstream.

## Phase 4: Batching as Flow Control
1. **Scenario 5 (buffer/window)**: Integrate batching scenarios from LAB-006 as a solution for backpressure (processing in groups).

## Phase 5: Documentation & Assessment
1. **CONCEPT.md**: Explain the "Demand Propagation" chain and Virtual Threads impact.
2. **README.md**: 
   - Command Dissection for `limitRate` and `onBackpressureBuffer`.
   - **Traceable Implementation**: Relative links to classes/tests.
   - Collapsible **Knowledge Check** section.

## Phase 6: Refinement (Constitution Audit v0.2.7)
1. **JUnit 5**: Remove `public` from test classes.
2. **AssertJ**: Use dedicated assertion methods.
3. **Advanced Tests**: Implement `ReplenishmentTest.java` to visualize the 75% rule.

## Constitution Compliance Check
- [x] Java 21+ syntax.
- [x] `StepVerifier` for everything.
- [x] Self-Assessment quiz (collapsible) included.
- [x] Relative links for traceability.
