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

---
## Technical Deep-Dive
Read the [CONCEPT.md](./CONCEPT.md) for a rigorous engineering deep-dive into the Reactive Pillars and the transition from blocking to non-blocking architectures.
