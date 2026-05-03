# LAB-003: Flux & Mono Foundations

Welcome to your first deep-dive into Project Reactor! In this lab, you will move beyond the raw Interfaces of Reactive Streams and learn to use the powerful abstractions: **Flux** and **Mono**.

## Learning Objectives
- Understand the difference between `Mono` (0-1) and `Flux` (0-N).
- Master the principle of **Lazy Execution**.
- Demonstrate **Pipeline Immutability**.
- Use **StepVerifier** to validate reactive signals.

---

## Scenario 1: The Lazy Greeter

Open `com.reactivelab.foundations.LazyGreeter`. In this exercise, we prove that reactive pipelines are just "blueprints" until someone subscribes.

### Execution
Run the following command to see the lazy execution test in action:

```bash
mvn test -Dtest=LazyGreeterTest
```

> [!NOTE]
> **Modern Context**: Even with the advent of **Virtual Threads** in Java 21, `Mono.fromCallable` remains a vital tool for orchestrating asynchronous workflows and integrating existing blocking logic into reactive pipelines.

### Command Dissection: `Mono.fromCallable()`
```bash
Mono.fromCallable(() -> "Hello")
```
- **Mono**: We expect at most one greeting.
- **fromCallable**: Unlike `Mono.just()`, this method is **lazy**. The lambda inside will NOT execute until the moment of subscription. This is ideal for wrapping blocking or expensive operations.

---

## Scenario 2: The Immutable Pipeline

Open `com.reactivelab.foundations.ImmutablePipeline`. A common mistake in Reactor is forgetting that operators do not mutate the current object.

### Execution
Run the test to see how ignoring return values leads to "invisible" transformations:

```bash
mvn test -Dtest=ImmutablePipelineTest
```

### Command Dissection: `map()`
```bash
flux.map(n -> n * 10)
```
- **Input**: A `Flux<Integer>`.
- **Output**: A **NEW** `Flux<Integer>` instance.
- **Why**: Immutability makes reactive pipelines thread-safe and predictable. Always chain your operators!

### Command Dissection: `Flux.just()`
```bash
Flux.just("A", "B", "C")
```
- **Flux**: Represents a sequence of 0 to N items.
- **just**: A factory method that captures fixed values at assembly time. It is **eager** regarding its arguments, but the emission is still lazy.

---

## Scenario 3: Validating Signals with StepVerifier

Open `com.reactivelab.foundations.StreamFactoriesTest`. Here we use `StepVerifier` to ensure our factories emit the correct signals.

### Execution
Run the factory tests:

```bash
mvn test -Dtest=StreamFactoriesTest
```

### Command Dissection: `StepVerifier`
```bash
StepVerifier.create(publisher)
    .expectNext("A")
    .verifyComplete();
```
- **create(publisher)**: Wraps the publisher for testing.
- **expectNext(value)**: Asserts that the next signal is an `onNext` with the specified value.
- **verifyComplete()**: This is crucial! It calls `subscribe()` and asserts that the final signal is `onComplete`.

---

## Cleanup
To clean the build artifacts, run:
```bash
mvn clean
```

This lab focuses on the core types of Project Reactor. In the next modules, we will explore more advanced operators and lifecycle control.

## 📝 Foundations Check (Self-Assessment)

Test your knowledge of the core Project Reactor foundations:

1. **Choosing the Type**: If you are implementing a `findById(id)` method in a repository, which type should you return? `Flux<User>` or `Mono<User>`?
   <details>
   <summary>💡 View Answer</summary>
   You should return **`Mono<User>`**. Since a primary key lookup returns at most one record, `Mono` correctly communicates the cardinality (0-1) of the result.
   </details>

2. **The Lazy Task**: You create a `Mono` using `Mono.fromCallable(() -> deleteEverything())`. You run your application, but the database remains intact. Why?
   <details>
   <summary>💡 View Answer</summary>
   Because **nothing happens until you subscribe**. If you just instantiate the `Mono` but never call `.subscribe()` (or return it to a framework that subscribes for you, like Spring WebFlux), the task is never executed.
   </details>

3. **Immutability Trap**: You write `flux.map(i -> i + 1)` on a separate line, then subscribe to the original `flux`. Will you see the incremented values?
   <details>
   <summary>💡 View Answer</summary>
   **No**. Operators do not modify the original Flux. They return a **new** instance. You must either chain the operators or capture the return value: `flux = flux.map(i -> i + 1);`.
   </details>

4. **The 1-Time Stream**: What happens if you create a `Flux` from a Java 8 `Stream` using `Flux.fromStream(stream)` and then subscribe to it twice?
   <details>
   <summary>💡 View Answer</summary>
   The second subscription will fail with an **`IllegalStateException`**. Unlike regular Fluxes, which can be re-subscribed to (re-running the source), a Flux backed by a Java `Stream` inherits the 1-time-use limitation of the underlying stream.
   </details>

---
**Next Step**: Once you've mastered the foundations, move to [LAB-004: Subscriptions & Lifecycle Control](../004-subscriptions-lifecycle/README.md).

