# Implementation Plan: LAB-000 The Reactive Mindset & Foundational Analogies

**Branch**: `000-reactive-mindset` | **Date**: 2026-04-30
**Input**: Specification from `docs/specs/000-reactive-mindset/spec.md`

## Summary

This lab establishes the foundational mindset required for the entire course. It focuses on conceptual understanding through analogies and a very simple "Push" vs "Pull" demonstration using RxJS. It serves as the gateway to the Reactive Manifesto (LAB-001).

## Phase 1: Monorepo Infrastructure (Base Setup)
1. **Scaffold**: Create `labs/000-reactive-mindset/`.
2. **Workspace Registration**: Add to `package.json` workspaces.
3. **Dependencies**: Initialize with `rxjs` and `jest`.

## Phase 2: Scenario 1 - Push vs Pull & I/O Blocking
1. Implement a script comparing a standard `let x = 1` vs an `Observable.of(1)`.
2. Introduce a "Blocking" simulation vs "Non-blocking" (Event Loop delegation).
3. **Validation**: Jest tests verifying the reactive nature of the stream.

## Phase 3: Scenario 2 - Errors as Signals & Backpressure
1. Implement a stream that throws an error and show how the `onError` handler catches it gracefully.
2. Conceptual demonstration of a "Fast Producer" and how Backpressure (even if just explained) relates to the system's Elasticity.

## Phase 4: Conceptual Documentation (The "Why")
1. **CONCEPT.md**: Deep dive into:
    - **The Pains**: Thread-per-request exhaustion (Java Servlet Model), Callback Hell, and the cognitive load of `CompletableFuture`.
    - **The Solutions**: Non-blocking I/O, declarative pipelines, and signal-based communication.
    - **The Analogies (Java focus)**: 
        - The Chef per Table (Thread-per-request) vs The Waiter (Event Loop).
        - The Phone Call (Blocking/Synchronous) vs The SMS/Post (Asynchronous/Message-driven).
        - Excel as the ultimate Reactive UI.
1. **README.md**: Create a step-by-step guide to running the "Push vs Pull" demo.
2. **Command Dissection**: Explain `npm test` and the structure of the validation test.

## Constitution Compliance Check
- [x] No `.sh` wrapper scripts.
- [x] Code comments explicitly describe what every test line validates.
- [x] Language used across all text is explicitly English.

## Open Questions
- Should we include a "Blocking" vs "Non-blocking" code example in Node.js (e.g., `fs.readFileSync` vs `fs.readFile`) here or leave it for LAB-001? (Proposed: Keep it simple here, focus on the "Stream" concept).
