# Lab Specification: LAB-008: Programmatic Stream Generation & Hot/Cold

**Feature Branch**: `008-programmatic-streams`
**Created**: 2026-05-03
**Status**: AUDITED
**Syllabus Section**: Level 2: The Core Spec & Advanced Control

## Syllabus Alignment *(mandatory)*

- **Concept**: Manual stream control and managing the subscription lifecycle (Cold vs Hot).
- **Prerequisites**: LAB-007: Threading Models & Schedulers.
- **Learning Objectives**:
  - LO-001: Generate sequences with `Flux.generate` and `Flux.create`.
  - LO-002: Implement standalone publishers using `Sinks.Many` and `Sinks.One`.
  - LO-003: Distinguish between **Cold** (Default) and **Hot** (Live) publishers.
  - LO-004: Multicast a stream to multiple subscribers using `share()`.
  - LO-005: Control the connection lifecycle with `publish().connect()`, `autoConnect()`, and `refCount()`.
  - LO-006: Implement replay strategies using `cache()`.
  - LO-007: Analyze thread-safety and race conditions in `Sinks`.

## Interactive Scenarios & Validation *(mandatory)*

### Scenario 1 - The Stateful Fibonacci (Priority: P1)
Implement the Fibonacci sequence using `Flux.generate`.
**Validation**: Verify the first 10 numbers.

### Scenario 2 - The Legacy Chat Bridge (Priority: P1)
Wrap a mock callback listener with `Flux.create` and handle `onDispose`.
**Validation**: Verify messages pushed to the listener reach the Flux.

### Scenario 3 - The Radio Broadcast (Cold vs Hot) (Priority: P1)
1. **Cold**: Create a Flux that emits "Song Line 1, 2, 3". Verify two subscribers both hear it from the start.
2. **Hot**: Transform it into a hot publisher using `publish().autoConnect()`. Verify that the second subscriber (late) misses initial lines.
**Validation**: Assert specific missed items for the second subscriber.

### Scenario 4 - The On-Demand Data Stream (Priority: P1)
Use `refCount(n)` to ensure the upstream only starts when `n` subscribers are present and stops when all leave.
**Validation**: Verify that the source is subscribed to and cancelled exactly when expected.

### Scenario 5 - The Efficient Cache (Priority: P2)
Use `cache(n)` to share a resource-intensive stream and replay the last `n` items to late subscribers.
**Validation**: Verify that the second subscriber receives the cached items instantly.

---

## Educational Requirements *(mandatory)*

### Concepts to Explain
- **EX-001**: **Generator vs Creator**: When to use `generate` (1-by-1 pull) vs `create` (multi-push).
- **EX-002**: **Hot vs Cold**: The analogy of a Movie (Cold) vs a Concert (Hot).
- **EX-003**: **Multicasting**: How `publish()` creates a `ConnectableFlux` to share a single upstream subscription.
- **EX-004**: **The "Ref" Count**: How `refCount` automates the start/stop logic based on demand.

### Technical Requirements
- **TR-001**: Use Java 21 and Project Reactor.
- TR-002: Use `StepVerifier` for all validations.
- TR-003: README MUST include an **Interactive Self-Assessment**.
- TR-004: **Traceable Implementation**: README MUST include relative links (from the project root) to Java classes and tests for each scenario.

## Success Criteria
- SC-001: Successful bridge of a callback API.
- SC-002: Demonstration of Hot vs Cold behavior differences.
- SC-003: Correct use of `refCount` for resource management.
- SC-004: All validation tests pass.
