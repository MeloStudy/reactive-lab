# Lab Specification: LAB-002: Flux & Mono Foundations

**Feature Branch**: `002-flux-mono-foundations`
**Created**: 2026-04-30 | **Refined**: 2026-05-02
**Status**: Audited (Refined v1.0.1)

**Syllabus Section**: Level 1: Foundations (Project Reactor)

## Syllabus Alignment *(mandatory)*

- **Concept**: Introduction to Project Reactor's core types (Flux & Mono), lazy execution, and factory methods.
- **Prerequisites**: LAB-006: The Reactive Streams Specification & TCK.
- **Learning Objectives**:
  - LO-001: Distinguish between `Mono<T>` (0-1) and `Flux<T>` (0-N) signal semantics.
  - LO-002: Verify the "Lazy Execution" principle: Nothing happens until you subscribe.
  - LO-003: Utilize core factory methods (`just`, `fromIterable`, `fromCallable`, `empty`, `error`).
  - LO-004: Demonstrate pipeline immutability and why operator chaining is required.

## Interactive Scenarios & Validation *(mandatory)*

### Scenario 1 - The Lazy Greeter (Priority: P1)

The learner will implement a `Mono<String>` using a `Callable` that performs a side effect (printing to console). They must observe that the side effect ONLY occurs upon subscription, not during instantiation.

**Validation (Automated Test)**: 
- Use `StepVerifier` to ensure the `Mono` emits the correct string.
- Use a mock or a flag to verify that the `Callable` was executed exactly once per subscription and zero times before subscription.

**Acceptance Scenarios**:
1. **Given** a `Mono.fromCallable(() -> "Hello")`, **When** no subscription exists, **Then** the callable is not executed.
2. **Given** a `Mono.fromCallable(() -> "Hello")`, **When** subscribed, **Then** the value "Hello" is emitted and the callable is executed.

---

### Scenario 2 - The Immutable Transformation (Priority: P1)

The learner will be given a `Flux<String>` of names. They must apply `map` and `filter` to transform the names. They must demonstrate that the original `Flux` remains unchanged and that a new `Flux` must be captured or chained.

**Validation (Automated Test)**:
- `StepVerifier` verifies the transformed stream.
- A separate `StepVerifier` verifies that the original source `Flux` still emits the original values.

---

### Scenario 3 - Factory Diversity (Priority: P2)

The learner will create several streams using different factory methods to handle collections, empty states, and errors.

**Validation (Automated Test)**:
- Verify `Flux.fromIterable` emits all elements.
- Verify `Mono.empty()` emits only `onComplete`.
- Verify `Mono.error()` emits `onError`.

---

## Educational Requirements *(mandatory)*

### Concepts to Explain

- **EX-001**: **Flux vs Mono**: The cardinality of reactive types and why we need both.
- **EX-002**: **Lazy Execution**: The assembly-time vs execution-time distinction.
- **EX-003**: **Immutability**: Why `flux.map(...)` doesn't change `flux`.
- **EX-004**: **Assembly vs Subscription**: Understanding that the pipeline is a "blueprint".
- **EX-005**: **Modern Context**: Understanding the role of Project Reactor in the era of Java 21+ Virtual Threads.

### Technical Requirements

- **TR-001**: Lab infrastructure MUST be containerized (though for this lab, standard Java 21 environment is sufficient).
- **TR-002**: Lab README MUST provide native orchestration commands (`mvn test`).
- **TR-003**: Lab MUST include automated validation tests using `StepVerifier` from `reactor-test`.
- **TR-004**: Reactive signals (`onNext`, `onError`, `onComplete`) MUST be explicitly validated.
- **TR-005**: Lab README MUST provide a "Command Dissection" for `Flux.just()`, `map()`, and `StepVerifier`.
- **TR-006**: Theoretical context MUST be provided in a `CONCEPT.md` file.

## Success Criteria *(measurable outcomes)*

- **SC-001**: Learner correctly identifies when a side effect in a lazy stream will execute.
- **SC-002**: Learner successfully chains operators without losing the reference to the transformed stream.
- **SC-003**: All `StepVerifier` tests pass.

## Assumptions

- Learner understands basic Java 21 syntax.
- Learner has completed the Reactive Streams Specification lab (LAB-006).
- Maven and JDK 21 are installed.
