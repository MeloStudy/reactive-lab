# Implementation Plan: LAB-019: Modern Observability (Micrometer Observation)

**Branch**: `019-modern-observability-micrometer` | **Date**: 2026-05-24
**Input**: Specification from `/specs/019-modern-observability-micrometer/spec.md`

## Summary

This lab focuses on the Micrometer Observation API in a Spring WebFlux application. The learner will instrument a reactive web service, use WebClient to make downstream calls, and understand how Reactor Context propagates the observation context. They will also learn to differentiate between high and low cardinality tags.

## Phase 1: Monorepo Infrastructure (Base Setup Cloning)
1. **Scaffold**: Clone the base setup from `labs/000-base-setup/`.
2. **Workspace Registration**: Add the new lab as a `<module>` in the root `pom.xml`.
3. **Data Requirements**: The lab will require a simple wiremock or a dummy downstream endpoint to demonstrate WebClient tracing propagation.
4. **Infrastructure**: Include a visualization stack (Prometheus + Zipkin) via Docker Compose to allow visual trace inspection.

## Phase 2: Scenario 1 - Instrumenting WebFlux Endpoints and WebClient
1. Guide the learner to add `micrometer-observation` and `micrometer-tracing` dependencies.
2. Configure `ObservationRegistry` and trace propagation for incoming WebFlux requests and outgoing WebClient calls.
3. Establish the testing mechanism using `TestObservationRegistry` and `StepVerifier` to ensure the context is correctly propagated.

## Phase 3: Scenario 2 - Custom Observation and Tagging Strategy
1. Introduce creating custom `Observation` instances around reactive service layers.
2. Teach the learner to apply `.lowCardinalityKeyValue()` and `.highCardinalityKeyValue()`.
3. Establish the validation mechanism to ensure the correct tags are recorded without breaking the reactive chain.

## Phase 4: Full Documentation & Dissection
1. Draft the `CONCEPT.md` to explain how Reactor Context handles `ThreadLocal` alternatives for Context Propagation, and what Micrometer Observation brings to the table.
2. Draft the `README.md` as an educational walkthrough. Enforce strict **Native Execution** logic.
3. Apply the **Command Dissection** pattern inside the `README.md` for new Micrometer/Observation configurations.

## Constitution Compliance Check
- [ ] No `.sh` wrapper scripts abstract the orchestration.
- [ ] Code comments explicitly describe what every test line validates.
- [ ] Language used across all text is explicitly English.
- [ ] **Dependency Governance**: The module correctly inherits from the parent POM and defines NO redundant versions.


