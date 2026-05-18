# Implementation Plan: LAB-013: Error Handling & Resilience in WebFlux [AUDITED]

**Branch**: `013-error-handling-resilience` | **Date**: 2026-05-18
**Input**: Specification from `docs/specs/013-error-handling-resilience/spec.md`
**Constitution Version**: v0.2.8

## Summary
The learner will build a resilient WebFlux application that handles errors at multiple levels (pipeline, controller, and global). The goal is to ensure the application remains responsive even under failure conditions and provides consistent error reporting to clients.

## Phase 1: Monorepo Infrastructure (Base Setup)
1. **Scaffold**: Create `labs/013-error-handling-resilience/` with standard Maven structure.
2. **Workspace Registration**: Add the lab to the root `pom.xml`.
3. **Core Dependencies**: Ensure `webflux` and `reactor-test` are present.
4. **Modular Architecture Package Setup**:
   - Establish production subpackages: `model`, `controller`, `router`, `service`, `filter`, and `exception`.
   - Organize all domain objects, services, routing configurations, telemetry filters, and global handles.

## Phase 2: Local & Controller Level Handling
1. **Instructional Path**:
   - Implementing `onErrorReturn` for simple fallbacks in `ResilienceController`.
   - Creating custom exceptions and using `@ExceptionHandler` in annotated controllers.
2. **Validation**: `WebTestClient` tests asserting specific status codes and body content for controlled failures.

## Phase 3: Global Resilience & Advanced Handlers
1. **Instructional Path**:
   - Implementing a `GlobalErrorWebExceptionHandler` using RFC 7807 `ProblemDetail`.
   - Propagating `Correlation-ID` from `Reactor Context` into the error response body.
   - **Reactor Upstream Propagation Constraint Resolution**: Since contextWrite in `CorrelationIdFilter` flows upstream, and `WebExceptionHandler` runs downstream outside the filter's subscription scope, we implement a hybrid lookup. It attempts context resolution first, falling back to exchange attributes.
   - Configuring server-side timeouts (`timeout()`) and exponential backoff retry strategies (`retryWhen()`).
   - Implementing error handling in `HandlerFilterFunction`.
2. **Validation**: Testing unhandled exceptions, context propagation, and filter-level failures.

## Phase 4: Full Documentation & Dissection
1. **CONCEPT.md**: Explain the signal-based nature of errors (`onError`), the reactive stack trace challenge, the order of precedence in Spring's error handling, Reactor Context propagation rules, and comparison with Java 21+ Project Loom Scoped Values.
2. **README.md**: Step-by-step guide with Command Dissections for `@RestControllerAdvice`, `AbstractErrorWebExceptionHandler`, `retryWhen`, and `Mono.deferContextual`.
3. **Interactive Self-Assessment**: Use collapsible `<details>` blocks for immediate pedagogical feedback.

## Constitution Compliance Check
- [x] No `.sh` wrapper scripts.
- [x] Code comments explicitly describe what every test line validates.
- [x] Language used across all text is explicitly English.
- [x] **Dependency Governance**: Inherits from root parent POM without shadowing.
- [x] **Modular Package Refactoring**: Fully compliant with enterprise subpackage requirements.

## Decision Summary
- **Decision on RFC 7807**: We use `ProblemDetail` as the primary error format to align with modern Spring Boot 3 standards.
- **Decision on Correlation ID Resolution**: Resolved empty-context WebExceptionHandler mapping using a hybrid Reactor Context + exchange attributes fallback to satisfy reactive theory and Spring WebFlux architecture.
