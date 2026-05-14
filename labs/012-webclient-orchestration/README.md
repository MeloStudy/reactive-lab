# LAB-012: WebClient: Orchestrating Downstream Services

Welcome to Lab 12! In this module, you will build a high-performance reactive gateway that orchestrates multiple microservices using `WebClient`.

## 🎯 Learning Objectives
- Configure a non-blocking `WebClient` with Netty.
- Master `retrieve()` vs `exchangeToMono()` for resource safety.
- Orchestrate parallel and sequential service calls.
- Implement resilience patterns (Timeouts, Retries, Fallbacks).
- Enforce non-blocking invariants using **BlockHound**.

## 🚀 Getting Started

### 1. Build the project
```bash
mvn clean install -pl labs/012-webclient-orchestration
```

### 2. Run the validation tests (Native Execution)
```bash
mvn test -pl labs/012-webclient-orchestration
```

## 🛠️ Hands-On: The Orchestrator

### Scenario 1: The Simple Fetcher
Fetch a user profile from a remote service using `.retrieve()`.
🔗 **Traceable Implementation**: [ReactiveOrchestrator.java](src/main/java/com/reactivelab/webclient/ReactiveOrchestrator.java) | [Test Suite](src/test/java/com/reactivelab/webclient/ReactiveOrchestratorTest.java)

### Scenario 2: Parallel Orchestration
Use `Mono.zip` to fetch User data and Order data simultaneously. Observe that the total time is equal to the slowest call.
🔗 **Traceable Implementation**: [ReactiveOrchestrator.java](src/main/java/com/reactivelab/webclient/ReactiveOrchestrator.java) | [Test Suite](src/test/java/com/reactivelab/webclient/ReactiveOrchestratorTest.java)

### Scenario 3: Dependent Calls
Use `flatMap` to chain calls where the second request depends on the result of the first.
🔗 **Traceable Implementation**: [ReactiveOrchestrator.java](src/main/java/com/reactivelab/webclient/ReactiveOrchestrator.java) | [Test Suite](src/test/java/com/reactivelab/webclient/ReactiveOrchestratorTest.java)

### Scenario 4: The Resilient Client
Implement a 2-second timeout and an exponential backoff retry strategy for unreliable services.
🔗 **Traceable Implementation**: [ReactiveOrchestrator.java](src/main/java/com/reactivelab/webclient/ReactiveOrchestrator.java) | [Test Suite](src/test/java/com/reactivelab/webclient/ReactiveOrchestratorTest.java)

### Scenario 5: Consuming the Stream
Handle a streaming endpoint and filter events on the fly.
🔗 **Traceable Implementation**: [ReactiveOrchestrator.java](src/main/java/com/reactivelab/webclient/ReactiveOrchestrator.java) | [Test Suite](src/test/java/com/reactivelab/webclient/ReactiveOrchestratorTest.java)

### Scenario 6: Advanced Body Control
Use `exchangeToMono` to inspect headers and manually manage body consumption to prevent memory leaks.
🔗 **Traceable Implementation**: [ReactiveOrchestrator.java](src/main/java/com/reactivelab/webclient/ReactiveOrchestrator.java) | [Test Suite](src/test/java/com/reactivelab/webclient/ReactiveOrchestratorTest.java)

> **Command Dissection: exchangeToMono()**
> - `exchangeToMono(response -> ...)`: Provides access to the full `ClientResponse`.
> - `response.releaseBody()`: Explicitly releases resources if the body is not needed. **Failure to do this or consume the body results in memory leaks.**

## 🧠 Self-Assessment
<details>
<summary>1. Why is <code>retrieve()</code> generally preferred over <code>exchange()</code>?</summary>
Because <code>retrieve()</code> handles resource management (body consumption and connection release) automatically, reducing the risk of memory leaks and connection pool exhaustion.
</details>

<details>
<summary>2. What is the impact of blocking an Event Loop thread?</summary>
Since WebClient uses a small number of threads to handle many concurrent connections, blocking one thread can stall hundreds of other requests and potentially deadlock the system.
</details>

<details>
<summary>3. How does BlockHound help in this lab?</summary>
BlockHound intercepts calls to blocking methods (like <code>Thread.sleep</code> or <code>FileInputStream.read</code>) when they are called from an Event Loop thread, helping you detect and fix accidental blocking code.
</details>

## ❓ Troubleshooting
- **`PoolAcquireTimeoutException`**: This usually happens when you use `exchange()` but fail to consume or release the response body, leading to connection leaks.
- **`BlockHoundRuntimeError`**: You are attempting to perform a blocking operation on a thread that is supposed to be non-blocking. Wrap blocking code in `publishOn(Schedulers.boundedElastic())` or refactor to use reactive alternatives.

## 🧹 Cleanup
The lab uses `MockWebServer` which shuts down automatically after tests.
