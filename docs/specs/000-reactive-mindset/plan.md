# Implementation Plan: LAB-000 The Reactive Mindset & Foundational Analogies

**Branch**: `000-reactive-mindset` | **Date**: 2026-04-30
**Input**: Specification from `docs/specs/000-reactive-mindset/spec.md`

## Summary

This lab establishes the foundational mindset required for the entire course. It focuses on conceptual understanding through analogies and a very simple "Push" vs "Pull" demonstration using RxJS. It serves as the gateway to the Reactive Manifesto (LAB-001).

## Phase 1: Conceptual Refinement (The Core)
1. **CONCEPT.md Audit**: Review and rewrite existing analogies to align with Java thread models.
2. **Signal Visualization**: Add Mermaid diagrams for signal propagation and backpressure.
3. **Wait State Deep-dive**: Explain blocking vs non-blocking using Java terminology.

## Phase 2: Instructional Design
1. **README.md Update**: Remove legacy execution steps.
2. **Interactive Elements**: Add a self-assessment quiz to the README to validate learning objectives.
3. **Navigation**: Ensure clear paths to LAB-001.

## Phase 4: Conceptual Documentation (The "Why")
1. **CONCEPT.md**: Deep dive into:
    - **The Pains**: Thread-per-request exhaustion (Java Servlet Model), Callback Hell, and the cognitive load of `CompletableFuture`.
    - **The Solutions**: Non-blocking I/O, declarative pipelines, and signal-based communication.
    - **The Analogies (Java focus)**: 
        - The Chef per Table (Thread-per-request) vs The Waiter (Event Loop).
        - The Phone Call (Blocking/Synchronous) vs The SMS/Post (Asynchronous/Message-driven).
        - Excel as the ultimate Reactive UI.
2. **README.md**: Create a step-by-step guide to running the "Push vs Pull" demo.
3. **Command Dissection**: Explain `npm test` and the structure of the validation test.

## Phase 3: Documentation & Audit
1. **CONCEPT.md**: Final review for engineering rigor.
2. **README.md**: Ensure consistent command dissection (if applicable) and clear instructions.

## Constitution Compliance Check
- [x] No `.sh` wrapper scripts.
- [x] Code comments explicitly describe what every test line validates.
- [x] Language used across all text is explicitly English.

## Open Questions
- Should we include a "Blocking" vs "Non-blocking" code example in Node.js (e.g., `fs.readFileSync` vs `fs.readFile`) here or leave it for LAB-001? (Proposed: Keep it simple here, focus on the "Stream" concept).
