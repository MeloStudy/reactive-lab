# Implementation Plan: LAB-007: Threading Models & Schedulers

**Branch**: `007-threading-schedulers` | **Date**: 2026-05-03
**Input**: Specification from `/docs/specs/007-threading-schedulers/spec.md`

## Phase 1: Project Setup
1. Create `labs/007-threading-schedulers`.
2. Configure `pom.xml` (Java 21, Reactor).
3. Package: `com.reactivelab.threading`.

## Phase 2: Threading Mechanics
1. **Scenario 1 (subscribeOn)**: Implement `BlockingService`. Show how `Thread.sleep` blocks the caller. Fix it with `subscribeOn(boundedElastic)`.
2. **Scenario 2 (publishOn)**: Implement `ComputeService`. Use `publishOn(parallel)` to move expensive `map` operations.
3. **Testing**: Use `Thread.currentThread().getName()` assertions within the pipeline to verify hops.

## Phase 3: The Context Bridge
1. **The Problem**: Create a `CorrelationIdManager` that uses `ThreadLocal`. Show it fails after a thread hop.
2. **The Solution**: Implement `ContextualService`. Use `.contextWrite()` to store a key and `Mono.deferContextual()` to retrieve it.
3. **Testing**: Verify the value survives a `publishOn(Schedulers.parallel())` jump.

## Phase 4: Execution Order & Traps
1. **Double SubscribeOn**: Create a test with two `subscribeOn` calls and prove only the first one wins.
2. **Multiple PublishOn**: Create a test with multiple `publishOn` calls and prove every call switches context.

## Phase 5: Documentation & Assessment
1. **CONCEPT.md**: Diagrams showing the "Current Thread" flow vs "Switching" flow. Explanation of `boundedElastic` vs `parallel`.
2. **README.md**: 
   - **Interactive Knowledge Check** (Quiz).
   - Command Dissection for `publishOn` vs `subscribeOn`.

## Constitution Compliance Check
- [x] Java 21+ syntax.
- [x] Clear thread name logging in tests.
- [x] `StepVerifier` for everything.
- [x] Self-Assessment quiz included.
