# Lab 001: The Reactive Manifesto & Asynchronous Paradigms

## Syllabus Alignment
This lab corresponds to **LAB-001** in the [Reactive Programming Lab Syllabus](../../docs/syllabus.md).

- **Module**: Level 1: The Reactive Mindset & Foundations (Node.js & RxJS)
- **Concept**: The Reactive Manifesto & Asynchronous Evolution (Callbacks vs. Promises vs. Observables).

## Introduction
Welcome to the first module of the Reactive Programming Lab. Before we dive into complex operators, we must understand the "Why" and the "How" of reactive systems.

In this lab, you will explore the 4 pillars of the **Reactive Manifesto** and see how **Observables** solve problems that Callbacks and Promises cannot.

## Prerequisites
- Node.js v20+
- Basic understanding of ES6 JavaScript

---

## Scenario 1: The Pillars of Reactive Systems

### The Problem: Brittle Legacy Systems
Legacy systems often use callbacks. If an asynchronous operation fails inside a callback without a proper try-catch, it can bubble up and crash the entire process. Furthermore, callbacks make it hard to enforce the "Resilience" pillar.

1.  Open `src/brittle-service.js`.
2.  Notice how the `processData` method throws an error inside a `setTimeout`.
3.  Run the validation test to see how the Reactive version handles this differently:
    ```bash
    npm test tests/scenario-1.test.js
    ```

### The Solution: Reactive Resilience
By using RxJS, we move to a **Message-Driven** approach where errors are treated as first-class signals (`onError`).

**Command Dissection: `catchError`**
| Operator | Purpose | Rationale |
| :--- | :--- | :--- |
| `catchError` | Intercepts an error signal in the source observable. | Ensures **Resilience** by providing a fallback or a graceful shutdown signal instead of crashing. |

---

## Scenario 2: From Single to Multiple (Promises vs Observables)

### The Limitation of Promises
A Promise is "one and done". It returns exactly one value or one error. What if you need to track the progress of a download? A Promise can't do that natively.

### The Power of Observables
An Observable is a stream. It can emit multiple values (`onNext`) before finally completing (`onComplete`).

1.  Check `src/data-fetcher.js`.
2.  Compare `fetchWithPromise()` vs `fetchWithObservable()`.
3.  Run the tests to verify the multiple emissions:
    ```bash
    npm test tests/scenario-2.test.js
    ```

**Command Dissection: `Observable` Constructor**
| Method | Description | Why? |
| :--- | :--- | :--- |
| `new Observable(subscriber => { ... })` | Manually creates a data stream. | Allows full control over the **Push** mechanism, enabling multiple `next` calls. |

---

## Technical Deep-Dive
For a rigorous explanation of the Reactive Manifesto and the internal mechanics of the Observer Pattern, please read [CONCEPT.md](./CONCEPT.md).

## Cleanup
To clean up any local artifacts (though none were created for this pure JS lab):
```bash
# No docker containers were used in this introductory lab.
# If they were, you would run:
# docker-compose down -v --remove-orphans
```
