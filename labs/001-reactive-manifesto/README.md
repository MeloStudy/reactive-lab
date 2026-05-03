# Lab 001: The Reactive Manifesto & Asynchronous Paradigms

## Syllabus Alignment
This lab corresponds to **LAB-001** in the [Reactive Programming Lab Syllabus](../../docs/syllabus.md).

- **Module**: Level 1: The Reactive Mindset & Foundations (Project Reactor)
- **Concept**: The Reactive Manifesto & Asynchronous Evolution (Callbacks vs. Futures vs. Flux/Mono).

## Introduction
Welcome to the Reactive Programming Lab. Before we code complex logic, we must master the **mindset**. Reactive programming is about moving from "Request/Response" (Pull) to "Streams/Events" (Push).

## 🧠 The 4 Pillars of the Manifesto

Read the [CONCEPT.md](./CONCEPT.md) for a deep dive into these pillars:

1.  **Responsive**: The system responds in a timely manner.
2.  **Resilient**: The system stays responsive in the face of failure.
3.  **Elastic**: The system stays responsive under varying workload.
4.  **Message Driven**: The system relies on asynchronous message-passing.

## 🛠️ Paradigms Shift in the JVM

| Paradigm | Java Tool | Model | Multi-value? |
| :--- | :--- | :--- | :--- |
| **Callbacks** | `Interface / Listener` | Push | Yes |
| **Futures** | `CompletableFuture<T>` | Push | No |
| **Reactive Streams** | `Flux<T> / Mono<T>` | **Push** | **Yes** |

## 🧪 What's Next?
This lab focuses on the theoretical foundation of the Manifesto. In the next modules, we will start implementing these concepts using **Project Reactor**.

## 📝 Manifesto Check (Self-Assessment)

Test your understanding of the Reactive Manifesto pillars and async evolution:

1. **Responsiveness vs. Resilience**: If a system is fast but crashes permanently when a single database node goes down, which pillar is it violating?
   <details>
   <summary>💡 View Answer</summary>
   It is violating **Resilience**. A resilient system must stay responsive even in the face of failure, typically through replication, isolation, and delegation.
   </details>

2. **The Elasticity Trigger**: What is the mechanism used in Reactive Streams to ensure that a "Fast Producer" doesn't overwhelm a "Slow Consumer"?
   <details>
   <summary>💡 View Answer</summary>
   **Backpressure** (or Flow Control). It allows the subscriber to signal the publisher how much data it is ready to process, preventing buffer overflows and out-of-memory errors.
   </details>

3. **Futures vs. Streams**: Why is a `CompletableFuture<List<User>>` less "reactive" than a `Flux<User>`?
   <details>
   <summary>💡 View Answer</summary>
   A `CompletableFuture` is **all-or-nothing** (it emits one signal when the whole list is ready). A `Flux` is a **stream** (it can emit users one by one as they arrive), allowing for better responsiveness and lower memory usage.
   </details>

4. **The Message Driven Boundary**: Why does the Reactive Manifesto emphasize "Message-Driven" rather than "Event-Driven"?
   <details>
   <summary>💡 View Answer</summary>
   Messages have a specific **destination**, whereas events are things that happened. Message-passing allows for location transparency, better isolation, and explicit backpressure between components.
   </details>

---
**Next Step**: Once you've mastered the manifesto, move to [LAB-002: The Reactive Streams Specification](../002-reactive-streams/README.md).
