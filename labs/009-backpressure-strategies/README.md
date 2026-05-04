# LAB-009: Backpressure Strategies & Flow Control

In this laboratory, you will learn how to handle scenarios where your data producer is faster than your consumer. You will move from a system that crashes under pressure to a resilient one using Project Reactor's flow control operators.

## Prerequisites
- Java 21+
- Maven
- Completed LAB-007 (Threading) and LAB-008 (Sinks)

---

## Scenario 1: The Overflowing Producer

Imagine a high-frequency sensor (the Producer) pushing data into your system. Your database (the Consumer) is much slower. Without backpressure, the system will eventually fail.

### 1.1 Reproducing the Crash
Open `src/test/java/com/reactivelab/backpressure/BackpressureTest.java`. We use a `Sinks.many().multicast().directBestEffort()` to simulate a producer that ignores demand.

If you run a stream without any backpressure operator and request 0, the elements will eventually fill up the internal buffers of the operators or the sink itself, causing an error.

### 1.2 Applying the Buffer Strategy
The `onBackpressureBuffer()` operator allows you to park elements in memory.

```java
Flux<Integer> bufferedFlux = producer.getFlux()
    .onBackpressureBuffer(10);
```

**Command Dissection: `onBackpressureBuffer`**
| Parameter | Description |
| :--- | :--- |
| `maxSize` | The number of elements to store before taking further action. |
| `onOverflow` | A callback executed when the buffer is full. |

**Rationale**: Use this when spikes are temporary. The buffer acts as a shock absorber.

---

## Scenario 2: Real-time Data Priority

In many real-world scenarios (like stock prices or mouse positions), an old value is useless if a newer one is available.

### 2.1 The Drop Strategy
Use `onBackpressureDrop()` to discard any element that arrives when the downstream is not ready.

```java
Flux<Integer> droppedFlux = producer.getFlux()
    .onBackpressureDrop(item -> log.info("Discarded: {}", item));
```

**Rationale**: This ensures your system stays responsive even under extreme load, at the cost of data loss.

### 2.2 The Latest Strategy
Use `onBackpressureLatest()` to keep only the most recent element.

```java
Flux<Integer> latestFlux = producer.getFlux()
    .onBackpressureLatest();
```

**Result**: If elements 1, 2, 3, 4, 5 arrive while demand is 0, when the subscriber finally requests 1 element, it will receive **5**.

---

## Scenario 3: Flow Control via Windowing

Sometimes, instead of dropping data, you want to process it in batches.

### 3.1 Using `buffer()`
Transform `Flux<T>` into `Flux<List<T>>`.

```java
flux.buffer(Duration.ofSeconds(1)) // Emit a list of elements every second
```

### 3.2 Using `window()`
Transform `Flux<T>` into `Flux<Flux<T>>`.

```java
flux.window(10) // Group elements into sub-fluxes of 10
```

---

## How to Run the Lab

1. **Run the Validation Tests**:
   Execute the following command to verify your understanding of backpressure strategies:
   ```bash
   mvn test -pl labs/009-backpressure-strategies
   ```

2. **Analyze the Output**:
   Look at the `log()` output in the console. Notice when `request(n)` signals are sent and how they correspond to `onNext(t)` signals.

## Atomic Cleanup
```bash
mvn clean -pl labs/009-backpressure-strategies
```
