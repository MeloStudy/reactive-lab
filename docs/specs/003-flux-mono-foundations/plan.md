# Implementation Plan: LAB-003: Flux & Mono Foundations

**Branch**: `003-flux-mono-foundations` | **Date**: 2026-04-30
**Input**: Specification from `/docs/specs/003-flux-mono-foundations/spec.md`

## Summary

The learner will transition from the raw Reactive Streams interfaces (LAB-003) to the high-level Project Reactor abstractions: `Flux` and `Mono`. This lab focuses on the "Passive Assembly" and "Active Subscription" phases, emphasizing that a reactive pipeline is an immutable blueprint until it is activated.

## Phase 1: Java Project Scaffolding
1. **Scaffold**: Create a Maven-based Java structure in `labs/003-flux-mono-foundations`.
2. **Dependencies**: 
   - `projectreactor:reactor-core`
   - `projectreactor:reactor-test` (for StepVerifier)
   - `junit-jupiter`
3. **Module Configuration**: Ensure Java 21 compatibility in `pom.xml`.

## Phase 2: Scenario 1 - Lazy Execution Mechanics
1. **Instructional Path**: Construct a `Mono` using `fromCallable` that increments a counter. Show that the counter remains at 0 after assembly and increments only after subscription.
2. **Testing**: `StepVerifier` will be used to trigger subscription and verify the final value and the counter state.

## Phase 3: Scenario 2 - Pipeline Immutability
1. **Instructional Path**: Provide a snippet where a learner tries to `map` a Flux but fails to use the return value. Then show the correct way (chaining or re-assignment).
2. **Testing**: Implement two tests: one verifying the source `Flux` (original state) and one verifying the result of the chained operations.

## Phase 4: Factory Methods & Signal Testing
1. **Instructional Path**: Exercise various factories: `Flux.just`, `Flux.fromIterable`, `Mono.empty`, `Mono.error`.
2. **Testing**: Use `StepVerifier` expectations like `expectNextCount`, `expectComplete`, and `expectError`.

## Phase 5: Documentation & Dissection
1. **CONCEPT.md**: Deep dive into the "Assembly Time vs Subscription Time" concept. Explain the internal `Publisher` wrapping that happens during operator application.
2. **README.md**: Step-by-step guide with `mvn test` commands.
3. **Command Dissection**: Breakdown of `StepVerifier` methods and core `Flux`/`Mono` factories.

## Phase 6: Refinement (Constitution v0.1.1)
1. **Depth**: Add "Signal Anatomy" to `CONCEPT.md`.
2. **Modernity**: Add brief Virtual Thread context to `CONCEPT.md` and `README.md`.
3. **Pedagogy**: Enhance test code documentation for line-by-line clarity.

## Constitution Compliance Check
- [x] No `.sh` wrapper scripts.
- [x] Code comments explicitly describe what every test line validates.
- [x] Maven native commands used throughout.
- [x] Modern Context (Virtual Threads) addressed.


## Open Questions
- None. (Resolved: Standard JDK 21 environment is sufficient; `log()` operator reserved for later labs).
