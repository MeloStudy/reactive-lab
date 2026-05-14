# Implementation Plan: LAB-016: Reactive Context & Tracing Propagation

**Branch**: `016-reactive-context-propagation` | **Date**: 2026-05-05
**Input**: Specification from `/specs/016-reactive-context-propagation/spec.md`

## Summary

This lab focuses on solving the problem of metadata propagation in non-blocking systems. Since threads are shared across multiple requests in WebFlux/Reactor, standard `ThreadLocal` storage (like SLF4J MDC) breaks. Learners will build a bridge between Reactor's `Context` and the logging system to enable consistent tracing.

## Phase 1: Monorepo Infrastructure (Base Setup)
1. **Scaffold**: Create `labs/016-reactive-context-propagation` as a new Maven module.
2. **Workspace Registration**: Add the module to the root `pom.xml`.
3. **Dependencies**: Ensure `reactor-core`, `reactor-test`, `logback-classic`, and `slf4j-api` are available via the parent POM.

## Phase 2: Scenario 1 - Correlation ID & MDC Bridge
1. **Instructional Path**:
   - Introduce `deferContextual` to read from the pipeline's context.
   - Show how to manually synchronize MDC before a log statement and clear it after (the "manual way").
   - **Note**: While `Micrometer Context Propagation` is the modern production standard, this lab focuses on raw Reactor `Context` to ensure learners understand the low-level mechanics. Micrometer will be mentioned as a production shortcut in the summary.
2. **Testing**:
   - Implement a test that runs a `Flux` through multiple `publishOn(Schedulers.parallel())` and verifies the `correlationId` survives the thread hops.

## Phase 3: Scenario 2 - Security Context Propagation
1. **Instructional Path**:
   - Use `contextWrite` at the bottom of the chain to "inject" a mock User object.
   - Retrieve it at the top of the chain (upstream) to demonstrate the propagation direction.
2. **Testing**:
   - A `StepVerifier` test that expects a specific `User` object to be present in the `ContextView` of a flatMap operation.

## Phase 4: Full Documentation & Dissection
1. **CONCEPT.md**:
   - Deep dive into `Context` internals (immutable maps).
   - Visualize the "Upward" flow of Context (from Subscriber to Publisher).
   - Explain the "Pollution" risk (why we shouldn't use Context for business data).
2. **README.md**:
   - Step-by-step guide to fixing "Empty Logs" in reactive pipelines.
   - **Command Dissection**: `.contextWrite()`, `.deferContextual()`, `MDC.put()`.

## Phase 5: Constitution v0.2.7 Refinement
1. **Instructional Path**:
   - Add Traceable Implementation links to `README.md` scenarios.
   - Update `CONCEPT.md` with Scoped Values vs Reactor Context (Modern Technology Assimilation).
2. **Validation**: Verify that relative links point to valid Java source files and tests.

## Constitution Compliance Check
- [ ] No `.sh` wrapper scripts.
- [ ] Code comments explicitly describe what every test line validates.
- [ ] Language used across all text is explicitly English.
- [ ] **Dependency Governance**: Inherits from root parent POM.


