# Implementation Plan: LAB-005: Essential Transformation Operators

**Branch**: `005-essential-operators` | **Date**: 2026-05-03
**Input**: Specification from `/docs/specs/005-essential-operators/spec.md`

## Phase 1: Project Setup
1. Create `labs/005-essential-operators`.
2. Configure `pom.xml` (Java 21, Reactor, AssertJ, Mockito).
3. Package: `com.reactivelab.operators`.

## Phase 2: Core Operators (Map & Filter)
1. **Instructional Path**: Create `UserSanitizer.java`. 
2. **Implementation**: Use `map(String::trim)` and `filter(s -> !s.isEmpty())`.
3. **Test**: `UserSanitizerTest.java` with simple `StepVerifier`.

## Phase 3: The Flattening Trio (flatMap, concatMap, switchMap)
1. **Async Enrichment**: Create `UserService.java` (returns `Mono<User>`).
2. **Interleaving**: Implement `UserEnricher.java` using `flatMap`. Prove out-of-order emission in tests using random delays.
3. **Sequencing**: Implement `FileService.java` using `concatMap`. Prove strict order.
4. **Cancellation**: Implement `SearchEngine.java` using `switchMap`. 

## Phase 4: Advanced Mechanics (Prefetch & Concurrency)
1. **Prefetch Lab**: Create `PrefetchObserver.java`.
2. **Implementation**: Use `flatMap(inner, concurrency, prefetch)`.
3. **Test**: Use `PublisherProbe` to count requests.

## Phase 5: Documentation & Assessment
1. **CONCEPT.md**: Visual diagrams of how `flatMap` merges inner streams. Explanation of prefetch buffers.
2. **README.md**: 
   - Command Dissection for `flatMap` overloads.
   - **Knowledge Check** section (Self-Assessment).

## Constitution Compliance Check
- [x] Java 21+ syntax.
- [x] No side-effects in operators (Functional purity).
- [x] `StepVerifier` for everything.
- [x] Self-Assessment quiz included.
