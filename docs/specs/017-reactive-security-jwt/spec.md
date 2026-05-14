# Lab Specification: LAB-017: Reactive Security & Identity (OAuth2/JWT)

**Feature Branch**: `017-reactive-security-jwt`
**Created**: 2026-05-14
**Status**: AUDITED
**Syllabus Section**: Level 3: Enterprise Reactive Services (Spring WebFlux)

## Syllabus Alignment *(mandatory)*

- **Concept**: `SecurityWebFilterChain`, `ReactiveSecurityContextHolder`, Non-blocking Authentication & Authorization.
- **Prerequisites**: LAB-011 (WebFlux Foundations), LAB-016 (Reactive Context Propagation).
- **Learning Objectives**:
  - LO-001: Configure a `SecurityWebFilterChain` for non-blocking HTTP security.
  - LO-002: Implement a reactive `AuthenticationManager` and `SecurityContextRepository` for stateless JWT validation.
  - LO-003: Understand how the `ReactiveSecurityContextHolder` uses Reactor Context to propagate security state.
  - LO-004: Apply Method-Level Security (`@PreAuthorize`) in a WebFlux environment.
  - LO-005: Evaluate architectural trade-offs between blocking Servlet Security (ThreadLocal) and WebFlux Security (Reactor Context).

## Interactive Scenarios & Validation *(mandatory)*

### Scenario 1 - Stateless JWT Authentication (Priority: P1)

Learners will implement a stateless authentication mechanism. They will create a WebFilter (or use Spring Security's native constructs) that intercepts an HTTP request, extracts a JWT from the `Authorization` header, and validates it reactively.

**Validation (Automated Test)**: A `WebTestClient` test that submits a valid JWT and expects a 200 OK, and submits an invalid/missing JWT and expects a 401 Unauthorized.

**Acceptance Scenarios**:

1. **Given** an endpoint `/api/secure/data`, **When** a GET request is made with a valid JWT, **Then** the request succeeds and returns the secure data.
2. **Given** the same endpoint, **When** no JWT or an expired JWT is provided, **Then** the response is 401 Unauthorized.

---

### Scenario 2 - Reactive Role-Based Authorization (Priority: P2)

Learners will secure an endpoint using `@PreAuthorize("hasRole('ADMIN')")`. They must ensure the JWT parsing logic correctly extracts roles and populates the `Authentication` object so that method-level security functions seamlessly without blocking the event loop.

**Validation (Automated Test)**: A `WebTestClient` test that submits a valid JWT with the `USER` role to an `ADMIN` endpoint and expects a 403 Forbidden.

---

## Educational Requirements *(mandatory)*

### Concepts to Explain

- **EX-001**: **SecurityWebFilterChain vs SecurityFilterChain**: Why the traditional filter chain cannot be used in a reactive application.
- **EX-002**: **ReactiveSecurityContextHolder**: How Spring Security leverages the Reactor `Context` (covered in LAB-016) to replace the `ThreadLocal`-based `SecurityContextHolder`.

### Technical Requirements

- **TR-001**: Lab infrastructure MUST be containerized strictly using **Docker / Docker Compose** (if an external identity provider is used, though this lab will focus on local JWT validation).
- **TR-002**: Lab README MUST provide native execution commands (e.g., `mvn test`).
- **TR-003**: Lab MUST include automated validation tests (Java/JUnit for Reactor/WebFlux labs).
- **TR-004**: Reactive signals MUST be explicitly validated in tests.
- **TR-005**: Lab README MUST provide a "Command Dissection" for any new WebFlux Security configurations.
- **TR-006**: Theoretical context MUST be provided in a `CONCEPT.md` file.
- **TR-007**: Lab README MUST include an **Interactive Self-Assessment** with collapsible `<details>` blocks.
- **TR-008**: Traceable Implementation relative links MUST be present in the README connecting theory directly to Java source code.

## Success Criteria *(measurable outcomes)*

- **SC-001**: Learner successfully secures a WebFlux application using JWTs without any blocking calls.
- **SC-002**: Learner correctly implements role-based access control (RBAC).
- **SC-003**: All validation tests (`WebTestClient`) pass.
- **SC-004**: Traceable Implementation links are provided for all scenarios.

## Assumptions

- Learner understands basic Spring Security concepts (Filters, Authentication, Authorization).
- Learner understands Reactor Context from LAB-016.
