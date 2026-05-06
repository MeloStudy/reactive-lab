# Implementation Plan: LAB-006: Combining & Basic Error Handling

**Branch**: `006-combining-errors` | **Date**: 2026-05-03
**Input**: Specification from `/docs/specs/006-combining-errors/spec.md`

## Phase 1: Project Setup
1. Create `labs/006-combining-errors`.
2. Configure `pom.xml` (Java 21, Reactor).
3. Package: `com.reactivelab.orchestration`.

## Phase 2: Combination Operators
1. **Scenario 1 (Zip)**: Implement `DashboardService`. Combine `Mono<User>` and `Mono<Long>` (friends) into `UserHeader`.
2. **Scenario 2 (Merge/Concat)**: Implement `SocialFeedService`. 
   - `merge`: Combine two `Flux` sources (Twitter, Instagram) and observe interleaving.
   - `concat`: Combine Cache and Remote sources and observe sequentiality.
3. **Testing**: Use `StepVerifier.withVirtualTime` to verify interleaving vs sequentiality.

## Phase 3: The Recovery Ladder
1. **Logging**: Implement `doOnError` in `ResilientClient`.
2. **Static Fallback**: Implement `onErrorReturn` for missing configuration.
3. **Translation**: Implement `onErrorMap` to hide internal stack traces.
4. **Dynamic Failover**: Implement `onErrorResume` to call a secondary service.

## Phase 4: Basic Resilience
1. **Retry Logic**: Implement `retry(n)` for a `Mono` representing an unstable network call.
2. **Testing**: Use `PublisherProbe` to verify how many times the source was subscribed to.

## Phase 5: Documentation & Assessment
1. **CONCEPT.md**: Explain the "Error Terminal signal" and the Marble Diagrams for `zip` and `merge`.
2. **README.md**: 
   - **Interactive Knowledge Check** (Quiz).
   - Command Dissection for `zip` and `onErrorResume`.

## Constitution Compliance Check
- [x] Java 21+ syntax.
- [x] No side-effects in logic (except `doOnError` for logging).
- [x] `StepVerifier` for everything.
- [x] Self-Assessment quiz included.
