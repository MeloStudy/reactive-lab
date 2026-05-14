# LAB-016: Reactive Context & Tracing Propagation

In this lab, you will master the art of metadata propagation in non-blocking environments. You will learn why `ThreadLocal` fails in reactive systems and how to use Reactor's `Context` to maintain tracing and security state across asynchronous boundaries.

## Prerequisites
- Completed LAB-011 (WebFlux Foundations).
- Understanding of Schedulers (LAB-007).

---

## Scenario 1: The Correlation ID Journey

In this scenario, you will implement a manual bridge between the Reactor `Context` and the SLF4J `MDC`. This is the fundamental pattern for ensuring that logs emitted from different threads during a single request share the same correlation ID.
🔗 **Traceable Implementation**: [ContextPropagationTest.java](src/test/java/com/reactivelab/context/ContextPropagationTest.java)

### Step 1: Examine the Pipeline
Open `src/test/java/com/reactivelab/context/ContextPropagationTest.java`. Note how the pipeline uses `publishOn(Schedulers.parallel())`. This forces a thread switch.

### Step 2: Accessing the Context
To read metadata from the subscription context, we use the `deferContextual` operator.

```java
.flatMap(event -> Mono.deferContextual(ctx -> {
    String cid = ctx.get("correlationId");
    // Bridge to MDC here
    return Mono.just(event);
}))
```

### Step 3: Writing to Context
Remember that `contextWrite` flows **upward**. You must place it at the end of the chain (closer to the subscriber) for the upstream operators to see it.

```java
.contextWrite(Context.of("correlationId", "trace-123"))
```

### Execution
Run the validation test:
```bash
mvn test -Dtest=ContextPropagationTest#shouldPropagateCorrelationIdAcrossSchedulers
```

---

## Scenario 2: Propagating Security State

Metadata isn't just for strings. You can store complex objects. In this scenario, we use a custom `User` record to simulate security context propagation.
🔗 **Traceable Implementation**: [ContextPropagationTest.java](src/test/java/com/reactivelab/context/ContextPropagationTest.java)

### Code Pattern
```java
Mono.deferContextual(ctx -> {
    User user = ctx.get(User.class);
    return Mono.just("Authorized: " + user.username());
})
.contextWrite(Context.of(User.class, new User("admin", "ROOT")));
```

### Execution
Run the validation test:
```bash
mvn test -Dtest=ContextPropagationTest#shouldRetrieveSecurityUserFromContext
```

---

## Command Dissection

| Operator | Rationale | Behavior |
| :--- | :--- | :--- |
| `.contextWrite(Context)` | **Inject Metadata** | Adds/Updates keys in the Context. Flows **upstream**. |
| `.deferContextual(Function)` | **Read Metadata** | Provides access to a `ContextView` to retrieve values. |
| `Context.of(key, value)` | **Creation** | Static factory for creating immutable context instances. |

---

## Self-Assessment

<details>
<summary>1. Why does ThreadLocal fail in Project Reactor?</summary>
Because a single thread is shared by many concurrent requests, and a single request can jump across multiple threads (Schedulers). ThreadLocal is strictly bound to a single thread and doesn't follow the data stream.
</details>

<details>
<summary>2. In which direction does Context propagate?</summary>
Upward. From the subscriber (bottom of the code) towards the publisher (top of the code).
</details>

<details>
<summary>3. Is the Reactor Context mutable?</summary>
No. It is immutable. Every `contextWrite` returns a new Context instance.
</details>

---

## Cleanup
Atomic cleanup is not required for this lab as it doesn't use external containers, but if you started any background processes:
```bash
mvn clean
```
