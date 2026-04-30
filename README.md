# Reactive Programming Masterclass Laboratory 🚀

Welcome to the **Reactive Programming Masterclass**. This repository contains a series of hands-on laboratories designed to master asynchronous stream processing, non-blocking I/O, and reactive systems design.

## 🧠 Why this curriculum? (The Pedagogical Path)
The transition to Reactive Programming requires a significant mindset shift. We approach this in progressive levels using **Java 21 and Project Reactor**:
1. **The Mindset**: Foundational concepts and analogies to understand "Push" vs "Pull" in the JVM.
2. **The Core**: Mastering the Reactive Streams Specification, Backpressure, and Schedulers.
3. **The Enterprise**: Building microservices using Spring WebFlux and R2DBC.
4. **The Scale**: We introduce Kafka, Observability, and advanced Resilience patterns.
5. **The Horizon**: We compare Reactive with Virtual Threads (Project Loom) to understand the modern Java landscape.

## 🎯 Learning Path Overview
For a detailed breakdown of all modules, learning objectives, and the certification path, see the [Full Syllabus](docs/syllabus.md).

- **Level 1: The Reactive Mindset & Foundations** (Project Reactor Foundations)
- **Level 2: The JVM Reactive Core** (Project Reactor, Backpressure, Threading)
- **Level 3: Enterprise Reactive Services** (Spring WebFlux, R2DBC, Security)
- **Level 4: Resilient & Event-Driven Systems** (Kafka, Micrometer, Resilience4j)
- **Level 5: The New Frontier & Extras** (Virtual Threads vs Reactive, Quarkus Mutiny)

*Note: The curriculum includes progressive mini-projects and a final Capstone Project to consolidate knowledge.*

## 🛠️ Prerequisites & Tech Stack
To successfully run these laboratories, you need the following environment:

- **Java 21 (LTS)**: Required for Project Reactor, Spring WebFlux, and Virtual Threads.
- **Maven 3.9+**: For managing Java projects.
- **Docker & Docker Compose**: Essential for containerized infrastructure (Kafka, Databases).
- **Make**: (Optional but recommended) For using the centralized command shortcuts.

## 🚀 How to use this Laboratory

Each folder in `labs/` is an independent module. Follow the instructions in the `README.md` of each lab.

### Standard Commands

We provide a `Makefile` at the root to simplify common tasks:

```bash
# Setup infrastructure for a lab
make setup LAB=001

# Run validation tests
make test LAB=001

# Cleanup environment
make clean LAB=001
```

## 🤖 Agent Workflows

The laboratory uses specialized Agent Workflows to automate quality gates and implementation phases. These are invoked via `/` commands:

- **`/lab-master-plan`**: Covers the entire design phase (Init + Architect). Use this to generate `spec.md`, `plan.md`, and `tasks.md`.
- **`/lab-master-build`**: Covers the entire execution phase (Builder + Auditor). Use this to generate the code, tests, and documentation once the plan is approved.
- **`/lab-refiner`**: Retrospectively audits and upgrades an already delivered laboratory to ensure compliance with the latest standards.

### Individual Workflows:
- `/lab-init`: Initiates a new lab and generates DRAFT artifacts.
- `/lab-architect`: Refines and validates planning (READY status).
- `/lab-builder`: Executes the technical implementation.
- `/lab-auditor`: Performs the final pedagogical gap audit (AUDITED status).

## 📜 Governance

The laboratory follows a strict set of engineering and pedagogical standards defined in the [Constitution](docs/constitution.md).

---
**Maintained by**: MeloStudy
**Version**: 0.2.0
