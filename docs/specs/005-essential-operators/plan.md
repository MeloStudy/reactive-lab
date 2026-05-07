# Implementation Plan: LAB-005: Essential Transformation & Filtering

**Branch**: `005-essential-operators` | **Date**: 2026-05-03
**Input**: Specification from `/docs/specs/005-essential-operators/spec.md`

## Phase 1: Project Setup
1. Create `labs/005-essential-operators`.
2. Package: `com.reactivelab.operators`.

## Phase 2: Core Operators (Map & Filter)
1. **Instructional Path**: Create `UserSanitizer.java`. 
2. **Implementation**: Use `map(String::trim)` and `filter(s -> !s.isEmpty())`.
3. **Test**: `UserSanitizerTest.java` with simple `StepVerifier`.

## Phase 3: The Flattening Trio (flatMap, concatMap, switchMap)
1. **Async Enrichment**: Implement `UserEnricher.java` using `flatMap`.
2. **Sequencing**: Implement `TaskRunner.java` using `concatMap`.
3. **Cancellation**: Implement `SearchDebouncer.java` using `switchMap`. 

## Phase 4: Slicing & Uniqueness (New)
1. **Instructional Path**: Create `DataStreamSlicer.java`.
2. **Implementation**: Use `skip(n)`, `take(n)`, and `distinct()`.
3. **Test**: `DataStreamSlicerTest.java` verifying exact window of unique items.

## Phase 5: Flux to Mono Transition
1. **Instructional Path**: Create `CollectionCollector.java`.
2. **Implementation**: Use `collectList()`, `collectMap()`, and `collectSortedList()`.
3. **Test**: `CollectionCollectorTest.java` verifying the `Mono<List<T>>` result.

## Phase 6: Documentation & Assessment
1. **CONCEPT.md**: Add sections for Filtering, Slicing, and Aggregation (Flux-to-Mono).
2. **README.md**: 
   - Command Dissection for `take`, `skip`, and `collectList`.
   - Updated **Knowledge Check** section.

## Constitution Compliance Check
- [x] Java 21+ syntax.
- [x] No side-effects in operators (Functional purity).
- [x] `StepVerifier` for everything.
- [x] Self-Assessment quiz included.
