# Implementation Plan: LAB-008: Programmatic Stream Generation

**Branch**: `008-programmatic-streams` | **Date**: 2026-05-03
**Input**: Specification from `/docs/specs/008-programmatic-streams/spec.md`

## Phase 1: Project Setup
1. Create `labs/008-programmatic-streams`.
2. Configure `pom.xml` (Java 21, Reactor).
3. Package: `com.reactivelab.generation`.

## Phase 2: Generating Sequences (Pull Model)
1. **Scenario 1 (generate)**: Implement `SequenceGenerator`. Focus on `Flux.generate(stateSupplier, generator)`.
2. **Testing**: Verify precision of sequences (e.g., first N numbers).

## Phase 3: Bridging APIs (Push Model)
1. **Scenario 2 (create)**: Implement `ChatBridge`. Wrap a mock `ChatService` callback.
2. **Scenario 3 (overflow)**: Add a test case that pushes 1000 items into a `create` sink with a slow subscriber and `LATEST` strategy.
3. **Resource Cleanup**: Ensure `sink.onDispose` is used to "disconnect" the mock service.

## Phase 4: Sinks (Event Buses)
1. **Scenario 4 (Sinks.Many)**: Implement `NotificationService`. 
2. **Scenario 5 (Sinks.One)**: Implement `AsyncSignal` for one-shot notifications.
3. **Testing**: Use multiple subscribers on a multicast sink.

## Phase 5: Documentation & Assessment
1. **CONCEPT.md**: Compare Generate (Synchronous/Pull) vs Create (Asynchronous/Push).
2. **README.md**: 
   - **Interactive Knowledge Check** (Quiz).
   - Command Dissection for `Sinks` and `Flux.create`.

## Constitution Compliance Check
- [x] Java 21+ syntax.
- [x] No side-effects in generators (except state management).
- [x] `StepVerifier` for everything.
- [x] Self-Assessment quiz included.
