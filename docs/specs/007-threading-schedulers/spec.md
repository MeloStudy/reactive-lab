# Lab Specification: LAB-007: Threading Models & Schedulers

**Feature Branch**: `007-threading-schedulers`
**Created**: 2026-05-03
**Status**: AUDITED
**Syllabus Section**: Level 2: The Core Spec & Advanced Control

## Syllabus Alignment *(mandatory)*

- **Concept**: Controlling execution context and ensuring safe state propagation across threads.
- **Prerequisites**: LAB-006: Combining & Basic Error Handling.
- **Learning Objectives**:
  - LO-001: Demonstrate the default single-threaded behavior of Reactive Streams.
  - LO-002: Master context switching for downstream operators with `publishOn`.
  - LO-003: Influence source execution context with `subscribeOn`.
  - LO-004: Select appropriate Schedulers (`parallel`, `boundedElastic`, `single`).
  - LO-005: Propagate immutable state across threads using the Reactor `Context`.
  - LO-006: **Virtual Threads (Loom)**: Understand how to use Virtual Thread Executors as Schedulers.

## Interactive Scenarios & Validation *(mandatory)*

### Scenario 1 - The Blocking Sin (Priority: P1)
The learner will start a stream that calls a blocking method (e.g., `Thread.sleep`). They must use `subscribeOn(Schedulers.boundedElastic())` to ensure the main event loop is not frozen.
**Validation**: Verify that the thread name of the source publisher belongs to the `boundedElastic` pool.

### Scenario 2 - The CPU Cruncher (Priority: P1)
The learner will perform a heavy computation (e.g., recursive Fibonacci) inside a `map`. They must use `publishOn(Schedulers.parallel())` to move the workload to a dedicated CPU pool.
**Validation**: Verify that operators BEFORE `publishOn` run on one thread, and operators AFTER run on a `parallel` thread.

### Scenario 3 - The Vanishing Identity (Priority: P1)
The learner will try to use a `ThreadLocal` value after a `publishOn` call and observe it returns `null`. They must refactor the code to use `.contextWrite()` and `deferContextual()` to propagate the value.
**Validation**: Verify that the value is successfully retrieved even after multiple thread hops.

### Scenario 4 - The Scheduler Trap (Priority: P1)
Demonstrate the "Immutable Upstream" rule: Using multiple `subscribeOn` calls and verifying that only the first one (closest to the source) has an effect.
**Validation**: Assert the thread name of the initial emission matches the FIRST scheduler.

### Scenario 5 - The Virtual Thread Alternative (Priority: P2)
Replace `boundedElastic` with a Virtual Thread Executor using `Schedulers.fromExecutor`.
**Validation**: Verify that the execution thread is a Virtual Thread.

---

## Educational Requirements *(mandatory)*

### Concepts to Explain
- **EX-001**: **The Assembly vs. Execution**: When the pipeline is built vs. when it is run.
- **EX-002**: **PublishOn vs. SubscribeOn**: The visual "downstream" vs "upstream" impact.
- **EX-003**: **The Scheduler Catalog**: Which pool to use for which task.
- **EX-004**: **The Context**: Why `ThreadLocal` is an anti-pattern in reactive systems.

### Technical Requirements
- **TR-001**: Use Java 21 and Project Reactor.
- **TR-002**: Use `StepVerifier` for all validations.
- **TR-003**: README MUST include an **Interactive Self-Assessment** (Constitution v0.1.2).
- **TR-004**: Use `doOnNext(v -> log.info(...))` to trace thread names.

## Success Criteria
- **SC-001**: Successful isolation of blocking code without freezing the pipeline.
- **SC-002**: Correct implementation of a multi-threaded data hop.
- **SC-003**: State successfully propagated across threads using Context.
- **SC-004**: All validation tests pass.
