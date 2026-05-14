# Implementation Plan: LAB-017: Reactive Security & Identity (OAuth2/JWT)

**Branch**: `017-reactive-security-jwt` | **Date**: 2026-05-14
**Input**: Specification from `docs/specs/017-reactive-security-jwt/spec.md`

## Summary

This lab tackles the complexities of securing a non-blocking Spring WebFlux application. Since `ThreadLocal` cannot be used to store security context across an Event Loop, learners will discover how Spring Security integrates with the Reactor `Context` to achieve stateless, non-blocking JWT authentication and role-based authorization.

## Phase 1: Monorepo Infrastructure (Base Setup)
1. **Scaffold**: Create the `labs/017-reactive-security-jwt` module.
2. **Workspace Registration**: Add the new lab as a `<module>` in the root `pom.xml`.
3. **Dependencies**: Include `spring-boot-starter-security`, `spring-boot-starter-webflux`, and `jjwt` (for JWT processing).

## Phase 2: Scenario 1 - Stateless JWT Authentication
1. **Instructional Path**:
   - Guide the learner to configure a `SecurityWebFilterChain` bean.
   - Implement a custom `ServerAuthenticationConverter` to extract the JWT from the `Authorization: Bearer <token>` header.
   - Implement a `ReactiveAuthenticationManager` to validate the token signature and emit an authenticated `Authentication` token.
2. **Testing Validation**: Write `WebTestClient` integration tests to assert that endpoints reject unauthenticated requests with a 401 Unauthorized status, and accept requests with a mocked, valid JWT.

## Phase 3: Scenario 2 - Reactive Role-Based Authorization
1. **Instructional Path**:
   - Enable `@EnableReactiveMethodSecurity`.
   - Modify the JWT validation logic to extract roles/authorities from the JWT claims.
   - Apply `@PreAuthorize("hasRole('ADMIN')")` to a specific handler or controller method.
2. **Testing Validation**: Write tests simulating a user with a `USER` role trying to access an `ADMIN` endpoint, verifying a 403 Forbidden response.

## Phase 4: Full Documentation & Dissection
1. **CONCEPT.md**:
   - Technical deep-dive into `ReactiveSecurityContextHolder` and how it delegates to the Reactor `Context` (tying directly into LAB-016).
   - Compare blocking Servlet Security (`Filter`) vs Reactive WebFlux Security (`WebFilter`).
2. **README.md**:
   - Step-by-step educational walkthrough.
   - **Command Dissection** for `@EnableReactiveMethodSecurity` and the `SecurityWebFilterChain` DSL.
   - **Traceable Implementation**: Relative links to the Java implementation and test suite.
   - **Self-Assessment**: Collapsible `<details>` blocks to test theoretical understanding.

## Constitution Compliance Check
- [ ] No `.sh` wrapper scripts abstract the orchestration.
- [ ] Code comments explicitly describe what every test line validates.
- [ ] Language used across all text is explicitly English.
- [ ] Traceable Implementation and Self-Assessment criteria met.

## Decisions
- **Authentication Scope**: The lab will strictly focus on *validating* incoming JWTs. We will use a mock token generator within the `WebTestClient` integration test suite to provide the tokens. This ensures the learner focuses entirely on the `ReactiveSecurityContextHolder` and WebFlux Security features, rather than getting distracted by login forms, password encoders, or external IdP setups.
