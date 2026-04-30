# Lab 001: The Reactive Manifesto & Asynchronous Paradigms

## Syllabus Alignment
This lab corresponds to **LAB-001** in the [Reactive Programming Lab Syllabus](../../docs/syllabus.md).

- **Module**: Level 1: The Reactive Mindset & Foundations (Node.js & RxJS)
- **Concept**: The Reactive Manifesto & Asynchronous Evolution (Callbacks vs. Promises vs. Observables).

## Introduction
Welcome to the Reactive Programming Lab. Before we code complex logic, we must master the **mindset**. Reactive programming is about moving from "Request/Response" (Pull) to "Streams/Events" (Push).

In this lab, you will visually observe the behavior of reactive systems through manual execution scripts, then validate them using automated tests.

## Prerequisites
- Node.js v20+ (LTS)
- No Docker is required for this introductory module.

---

## 🛠️ Step 1: Visualizing the Manifesto

### Scenario 1 - Resilience & Responsiveness
Legacy callback systems are brittle. An unhandled error in a callback can crash the entire execution stack. Reactive systems treat errors as signals.

**Manual Execution:**
Run the following script to see how a "Brittle" service crashes vs. how a "Reactive" service survives:
```bash
node src/scenarios/manifesto.js
```

**What to look for:**
- Observe the red `[ERROR]` from the legacy service.
- Observe the purple `[SIGNAL]` and final green `[SUCCESS]` from the reactive service.

---

## 🛠️ Step 2: Paradigms Shift

### Scenario 2 - Promises vs Observables
A Promise handles a single future value. An Observable handles a stream.

**Manual Execution:**
Run this script to see the difference in emission count:
```bash
node src/scenarios/paradigms.js
```

**What to look for:**
- The Promise emits only once.
- The Observable emits multiple `PROGRESS` signals before the final `DATA`.

---

## 🛠️ Step 3: Elasticity & Flow Control

### Scenario 3 - The Slow Consumer (Backpressure)
In reactive systems, producers and consumers are decoupled. If a producer is too fast, the consumer must have a strategy to handle the load without crashing.

**Manual Execution:**
Observe how the system handles a burst of 5 messages sent every 50ms to a consumer that takes 200ms to process each:
```bash
node src/scenarios/elasticity.js
```

---

## 🧪 Automated Validation
Once you have observed the behaviors manually, run the TDD suite to confirm technical correctness:

```bash
# Run all tests
npm test

# Run a specific scenario
npm test tests/scenario-3.test.js
```

---

## 📖 Command Dissection

| Command/Operator | Purpose | Reactive Pillar |
| :--- | :--- | :--- |
| `catchError` | Intercepts error signals and provides fallback. | **Resilience** |
| `concatMap` | Processes items sequentially, ensuring order and flow control. | **Elasticity** |
| `new Observable()` | Creates a custom stream with full Push control. | **Message Driven** |

## Technical Deep-Dive
Read the [CONCEPT.md](./CONCEPT.md) for a rigorous engineering deep-dive into the Event Loop and the Observer pattern.
