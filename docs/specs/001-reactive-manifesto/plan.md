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
3. Test: A Jest test that asserts the system doesn't block and handles a simulated failure gracefully.

## Phase 3: Scenario 2 - Promises vs Observables
1. Implement a function `fetchDataWithProgress()` that returns an Observable.
2. The learner must subscribe to this and log both progress and result.
3. Test: Use RxJS `TestScheduler` to verify the timing and sequence of emissions.

## Phase 4: Full Documentation & Dissection
1. Draft `CONCEPT.md`: Detailed breakdown of Responsiveness, Resilience, Elasticity, and Message-Driven. Comparison table of Callback vs Promise vs Observable.
2. Draft `README.md`: Educational walkthrough leading from a "Synchronous/Blocking" mindset to a "Reactive/Streaming" one.
3. Apply **Command Dissection** for `npm test` and basic RxJS Observable creation methods.

## Constitution Compliance Check
- [ ] No `.sh` wrapper scripts.
- [ ] Code comments explicitly describe what every test line validates.
- [ ] Language used across all text is explicitly English.

## Open Questions (Resolved)
- **Visualization**: We will rely on descriptive console logs following a specific format to show stream lifecycle signals (`[ON_NEXT]`, `[ERROR]`, `[COMPLETE]`) to keep dependencies low for the first lab.
- **Observable Creation**: We will start with the raw `new Observable()` constructor in the `README.md` to explicitly demonstrate the push-based observer pattern, then introduce `of`/`from` as syntactic sugar in the summary.
- **Simulation**: Simulation will be kept within the Node.js process using `setTimeout` and `Promise.reject` to demonstrate basic async paradigms without requiring external Docker dependencies for this introductory module.
