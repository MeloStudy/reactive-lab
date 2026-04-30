# Implementation Plan: LAB-001: The Reactive Manifesto & Asynchronous Paradigms

**Branch**: `001-reactive-manifesto` | **Date**: 2026-04-30
**Input**: Specification from `/specs/001-reactive-manifesto/spec.md`

## Summary

The learner will explore the fundamental "Why" behind reactive programming by mapping the Reactive Manifesto pillars to code. They will then transition from traditional asynchronous patterns (Promises) to reactive streams using RxJS.

## Phase 1: Monorepo Infrastructure (Base Setup Cloning)
1. **Scaffold**: Create `labs/001-reactive-manifesto/` using `labs/000-base-setup/` as a template (once base setup exists).
2. **Workspace Registration**: Update `package.json` in the root to include the new lab if using NPM workspaces.
3. **Data Requirements**: This lab uses a simulated "Remote Service" that emits values with delays to demonstrate async paradigms.

## Phase 2: Scenario 1 - The Reactive Manifesto in Code
1. Define a "Legacy" service using callbacks that is brittle.
2. Provide a "Reactive" version using RxJS that demonstrates Resilience (error isolation) and Responsiveness.
3. **Observability**: Create `src/scenarios/manifesto.js` for manual execution.
4. Test: A Jest test that asserts the system doesn't block and handles a simulated failure gracefully.

## Phase 3: Scenario 2 & 3 - Paradigms and Elasticity
1. Implement `src/scenarios/paradigms.js` to contrast Promise vs Observable manually.
2. Implement `src/scenarios/elasticity.js` to show a fast producer/slow consumer burst.
3. Test: Use `rxjs/testing` TestScheduler for deterministic virtual time validation in `tests/scenario-2.test.js`.

## Phase 4: Full Documentation & Dissection (Deep Rigor)
1. Draft `CONCEPT.md`: Deep dive into **Event Loop vs Blocking Threads**. Add a **Mermaid** diagram for the Observer pattern lifecycle. Explain why the "Push" model is superior for non-blocking I/O.
2. Draft `README.md`: Educational walkthrough that emphasizes running the manual scripts first to see the behavior, followed by automated tests for validation.
3. Apply **Command Dissection** for `npm test` and basic RxJS Observable creation methods.

## Constitution Compliance Check
- [ ] No `.sh` wrapper scripts.
- [ ] Code comments explicitly describe what every test line validates.
- [ ] Language used across all text is explicitly English.

## Open Questions (Resolved)
- **Visualization**: We will rely on descriptive console logs following a specific format to show stream lifecycle signals (`[ON_NEXT]`, `[ERROR]`, `[COMPLETE]`) to keep dependencies low for the first lab.
- **Observable Creation**: We will start with the raw `new Observable()` constructor in the `README.md` to explicitly demonstrate the push-based observer pattern, then introduce `of`/`from` as syntactic sugar in the summary.
- **Simulation**: Simulation will be kept within the Node.js process using `setTimeout` and `Promise.reject` to demonstrate basic async paradigms without requiring external Docker dependencies for this introductory module.
