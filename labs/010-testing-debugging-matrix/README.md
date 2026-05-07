# LAB-010: Testing & Debugging Matrix 🧪🛡️

Welcome to the Testing and Debugging Matrix. In this laboratory, you will master the tools required to verify complex asynchronous behavior, warp time to your advantage, and protect your event loops from blocking code.

## 🎯 Learning Objectives
- **Solve the Assembly vs Execution gap** using global debug hooks and checkpoints.
- **Warp Time** using `withVirtualTime` to test long-duration streams instantly.
- **Verify Branching Logic** using `PublisherProbe` to spy on "silent" execution paths.
- **Master state propagation** across thread hops using **Reactor Context**.
- **Protect the Event Loop** by enforcing non-blocking execution with **BlockHound**.

---

## 🛠️ Command Dissection

### 1. `StepVerifier.withVirtualTime(() -> Flux)`
Hijacks the global clock for the duration of the test.
- **Wait Requirement**: Must use `thenAwait(Duration)` to advance the virtual clock.
- **Rule**: The Flux must be created *inside* the supplier for the hijacking to be effective.

### 2. `Hooks.onOperatorDebug()`
Globally enables "Assembly Stacktrace" capturing.
- **Value**: When an error occurs, the stack trace will include the exact line number where the failing operator was declared.

### 3. `checkpoint("label")`
Adds a manual label to the pipeline state.
- **Usage**: Lightweight alternative to global debug hooks for specific hotspots.

### 4. `PublisherProbe.of(publisher)`
Creates a spy wrapper around a publisher.
- **Verification**: Allows you to assert `probe.assertWasSubscribed()` or `probe.assertWasRequested()` even if no data was emitted.

---

## 🧪 Scenarios

### Scenario 1: The Time Traveler
A stream emits data once per day for 365 days. Verify the entire year of data in milliseconds.
- **Key Operator**: `withVirtualTime`.

### Scenario 2: The Branch Spy
Verify that a `switchIfEmpty` fallback is triggered when the primary source is empty.
- **Key Operator**: `PublisherProbe`.

### Scenario 3: The Correlation ID (Context)
Pass a Trace ID through a pipeline that hops between `parallel` and `boundedElastic` schedulers.
- **Key Operator**: `contextWrite`.

### Scenario 4: The BlockHound Sentry
Inject a blocking `Thread.sleep` and verify that the system detects it and fails the test.
- **Key Operator**: `BlockHound.install()`.

### Scenario 5: The Labeled Pipeline
Inject a failure into a complex pipeline and use `checkpoint` to identify which segment failed.
- **Key Operator**: `checkpoint`.

---

## 🧠 Self-Assessment Quiz

1. **Why does a standard Java stack trace often seem "incomplete" in Reactor?**
   - *Answer: It only shows the Execution stack. The Assembly stack (where the operator was defined) is lost because assembly and execution happen on different threads/times.*

2. **What is the main drawback of `Hooks.onOperatorDebug()`?**
   - *Answer: Significant performance and memory overhead, as it must capture a stack trace for every operator instantiation.*

3. **In which direction does Reactor Context travel?**
   - *Answer: Upstream (from Subscriber to Publisher) during the subscription phase.*

4. **Why do we need a `Supplier` for `withVirtualTime`?**
   - *Answer: To ensure the operators are created AFTER the virtual scheduler has been installed as the default.*

5. **What interface must a thread implement for BlockHound to monitor it?**
   - *Answer: `io.projectreactor.core.scheduler.NonBlocking` (or be part of a scheduler that marks its threads as such).*

---

## 🚀 Execution Guide
```bash
# Run all testing matrix validation tests
mvn test -pl labs/010-testing-debugging-matrix
```

## 🧹 Cleanup
```bash
mvn clean -pl labs/010-testing-debugging-matrix
```
