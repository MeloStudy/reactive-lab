# Implementation Plan: LAB-013: Error Handling & Resilience in WebFlux [AUDITED]

**Branch**: `013-error-handling-resilience` | **Date**: 2026-05-04
**Input**: Specification from `docs/specs/013-error-handling-resilience/spec.md`

## Summary

The learner will build a resilient WebFlux application that handles errors at multiple levels (pipeline, controller, and global). The goal is to ensure the application remains responsive even under failure conditions and provides consistent error reporting to clients.

## Phase 1: Monorepo Infrastructure (Base Setup)
1. **Scaffold**: Create `labs/013-error-handling-resilience/` with standard Maven structure.
2. **Workspace Registration**: Add the lab to the root `pom.xml`.
3. **Core Dependencies**: Ensure `webflux` and `reactor-test` are present.

## Phase 2: Local & Controller Level Handling
1. **Instructional Path**:
   - Implementing `onErrorReturn` for simple fallbacks.
   - Creating custom exceptions and using `@RestControllerAdvice` with `@ExceptionHandler`.
2. **Validation**: `WebTestClient` tests asserting specific status codes and body content for controlled failures.

## Phase 3: Global Resilience & Advanced Handlers
1. **Instructional Path**:
   - Implementing a `GlobalErrorWebExceptionHandler` using `ProblemDetail`.
   - Propagating `Correlation-ID` from `Reactor Context` into the error response body.
   - Configuring server-side timeouts and retry strategies.
   - Implementing error handling in `HandlerFilterFunction`.
2. **Validation**: Testing unhandled exceptions, context propagation, and filter-level failures.

## Phase 4: Full Documentation & Dissection
1. **CONCEPT.md**: Explain the signal-based nature of errors (`onError`), the reactive stack trace challenge, and the order of precedence in Spring's error handling.
2. **README.md**: Step-by-step guide with Command Dissections for `@RestControllerAdvice`, `AbstractErrorWebExceptionHandler`, and `retryWhen`.

## Constitution Compliance Check
- [ ] No `.sh` wrapper scripts.
- [ ] Code comments explicitly describe what every test line validates.
- [ ] Language used across all text is explicitly English.
- [ ] **Dependency Governance**: Inherits from root parent POM.

## Open Questions
- **Decision on RFC 7807**: We will use `ProblemDetail` as the primary error format to align with modern Spring Boot 3 standards.
- **Decision on Custom Attributes**: We will customize `DefaultErrorAttributes` specifically to extract and include the `Correlation-ID` from the Reactor Context.
