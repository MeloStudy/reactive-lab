# LAB-006: The Reactive Streams Specification & TCK

Welcome to the foundation of modern reactive programming on the JVM. In this lab, you will step away from high-level libraries like Project Reactor and build your own **Publisher** from scratch using the raw Reactive Streams interfaces.

## Learning Objectives

1.  **Implement** the core `Publisher` and `Subscription` interfaces.
2.  **Master** the "Handshake" sequence: Subscription -> Request -> Next.
3.  **Validate** compliance using the official Technology Compatibility Kit (TCK).

## Prerequisites

- Java 21+
- Maven 3.9+

---

## Step 1: Implementation of the "Raw" Contract

Your first task is to implement a `CustomPublisher` that emits a range of integers. Open `com.reactivelab.spec.CustomPublisher` and examine the implementation.

### The Handshake Logic

Notice how the `subscribe` method immediately calls `onSubscribe`. This is the "Handshake".

```java
@Override
public void subscribe(Subscriber<? super Integer> subscriber) {
    subscriber.onSubscribe(new CustomSubscription(subscriber, count));
}
```

### The Subscription State

The `CustomSubscription` class is the engine. It must track **Demand** using an `AtomicLong`. No items are emitted unless `demand > 0`.

## Step 2: Running the Handshake Validation

We have provided a manual test suite to verify your understanding of the protocol.

### Execution

```bash
mvn test -Dtest=HandshakeTest
```

### Analysis
The tests verify three critical scenarios:
1.  **Cold Start**: No data flows until `request` is called.
2.  **Demand Flow**: Data flows in chunks matching the `request(n)` calls.
3.  **Cancellation**: The stream stops immediately when `cancel()` is called.

---

## Step 3: The TCK Rigor

Passing your own tests is easy. Passing the official **Technology Compatibility Kit (TCK)** is much harder. The TCK checks for hundreds of rules, including thread safety and edge cases.

### Execution

```bash
mvn test -Dtest=PublisherTCKTest
```

| Command | Purpose |
| :--- | :--- |
| `mvn test` | Runs all tests in the project. |
| `-Dtest=PublisherTCKTest` | Filters execution to only the TCK validation class. |

### Command Dissection: Maven Test Output

| Element | Description |
| :--- | :--- |
| `Tests run: 38` | The number of compliance rules checked by the TCK. |
| `Failures: 0` | If this is non-zero, your implementation violates a specific spec rule. |
| `Skipped: 9` | Optional rules or tests for unimplemented features (like failed publishers). |

---

## Challenge: Breaking the Rules

Try modifying your `CustomPublisher` to send an item *before* the subscriber requests it. Run the TCK again and observe the failure. This demonstrates why the specification is so strict: it guarantees that consumers are never overwhelmed.
