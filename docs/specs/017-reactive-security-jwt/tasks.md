# Tasks: LAB-017: Reactive Security & Identity (OAuth2/JWT)

**Input**: Design documents from `docs/specs/017-reactive-security-jwt/`
**Prerequisites**: plan.md (required), spec.md (required).

**Validation Goal**: This project follows strict "TDD for Learning". The automated tests (JUnit/WebTestClient) MUST ensure exactly what the README teaches.

## Phase 1: Monorepo Setup & Configuration
- [x] T001 Scaffold lab directory `labs/017-reactive-security-jwt`.
- [x] T002 Update local root `pom.xml` to include the new module.
- [x] T003 Configure `pom.xml` dependencies (`spring-boot-starter-security`, `spring-boot-starter-webflux`, `jjwt`).

## Phase 2: Validation Framework Implementation (TDD)
- [x] T004 Write validation script logic (e.g., `SecurityIntegrationTest.java`) to cover Scenario 1 (Stateless Authentication) outcomes (401 vs 200).
- [x] T005 Write validation script logic to cover Scenario 2 (Role-Based Authorization) outcomes (403 vs 200).
- [x] T006 Provide required codebase comments on what each test step validates.

## Phase 3: Application Implementation
- [x] T007 Implement the JWT utility class to generate and parse tokens (for test facilitation).
- [x] T008 Implement `ServerAuthenticationConverter` to extract the Bearer token.
- [x] T009 Implement `ReactiveAuthenticationManager` to validate the JWT and issue an `Authentication` token.
- [x] T010 Configure the `SecurityWebFilterChain` to wire the components together and define authorization rules.
- [x] T011 Create a secure REST Controller with `@PreAuthorize` method security.

## Phase 4: Educational Content & Hands-On Environment
- [x] T012 Write the theoretical `CONCEPT.md` detailing the "Why" of `ReactiveSecurityContextHolder` vs `ThreadLocal`.
- [x] T013 Write the step-by-step native `README.md` guide for Scenario 1 and 2.
- [x] T014 Inject **Command Dissection** blocks into `README.md`.
- [x] T015 Ensure **Traceable Implementation** relative links are present in `README.md`.
- [x] T016 Implement the **Self-Assessment** `<details>` section in `README.md`.

## Phase 5: Final Certification
- [x] T017 Verify all tests pass with `mvn test`.
- [x] T018 Verify compliance with the Constitution v0.2.7.
- [x] T019 Update `docs/syllabus.md` status to `AUDITED`.
