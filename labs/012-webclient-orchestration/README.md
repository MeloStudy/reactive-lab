# LAB-012: WebClient: Orchestrating Downstream Services

Welcome to Lab 12! In this module, you will build a high-performance reactive gateway that orchestrates multiple external microservices using **WebClient**. You will master non-blocking service-to-service communication, sequential vs. parallel stream composition, resilience engineering, and strict event-loop safety.

---

## 🎯 Learning Objectives
- Configure a non-blocking `WebClient` instance via standard Builder patterns.
- Contrast traditional **Thread-per-Request** blocking models with Netty's **Event Loop Thread Delegation**.
- Master **`retrieve()`** vs. **`exchangeToMono()`** to maintain absolute memory safety.
- Orchestrate concurrent calls via `Mono.zip()` and sequential pipelines via `flatMap()`.
- Implement robust resilience policies (Timeouts, Retries with backoff, Fallbacks).
- Detect and prevent JVM thread-blocking using **BlockHound** diagnostics.

---

## 🚀 Getting Started

### 1. Build the project
Run the following Maven clean install command from the root directory:
```bash
mvn clean install -pl labs/012-webclient-orchestration
```

### 2. Run the validation tests (Native Execution)
Ensure your environment executes the test suite successfully:
```bash
mvn test -pl labs/012-webclient-orchestration
```

---

## 🛠️ Hands-On: The Orchestration Scenarios

### Scenario 1: The Base Fetcher
Fetch a user profile by ID from a mock downstream service using the safe `.retrieve()` API, mapping the JSON body directly to a `Mono<User>`.
- 🔗 **Traceable Implementation**: [ReactiveOrchestrator.java](src/main/java/com/reactivelab/webclient/service/ReactiveOrchestrator.java) | [UserController.java](src/main/java/com/reactivelab/webclient/controller/UserController.java) | [User.java](src/main/java/com/reactivelab/webclient/model/User.java) | [Test Suite](src/test/java/com/reactivelab/webclient/service/ReactiveOrchestratorTest.java)

### Scenario 2: Parallel Orchestration
Trigger calls to both User and Order services concurrently. Using `Mono.zip()`, merge their asynchronous payloads into a unified `UserDashboard` payload. 
- *Pedagogical Verification*: Observe that the total execution time matches the slowest service (~500ms), proving non-blocking concurrent request interleaving.
- 🔗 **Traceable Implementation**: [ReactiveOrchestrator.java](src/main/java/com/reactivelab/webclient/service/ReactiveOrchestrator.java) | [UserDashboardController.java](src/main/java/com/reactivelab/webclient/controller/UserDashboardController.java) | [UserDashboard.java](src/main/java/com/reactivelab/webclient/model/UserDashboard.java) | [Test Suite](src/test/java/com/reactivelab/webclient/service/ReactiveOrchestratorTest.java)

### Scenario 3: The Dependency Chain
Perform sequential asynchronous orchestration. Retrieve the User profile first, extract their `preferenceId`, and use it to execute a dependent call to the Preferences Service. Finally, aggregate all results.
- 🔗 **Traceable Implementation**: [ReactiveOrchestrator.java](src/main/java/com/reactivelab/webclient/service/ReactiveOrchestrator.java) | [UserDashboardController.java](src/main/java/com/reactivelab/webclient/controller/UserDashboardController.java) | [Preference.java](src/main/java/com/reactivelab/webclient/model/Preference.java) | [Test Suite](src/test/java/com/reactivelab/webclient/service/ReactiveOrchestratorTest.java)

### Scenario 4: The Resilient Client
Integrate network safety nets. When calling a flaky downstream service, configure a strict 2-second timeout and an intelligent exponential backoff retry mechanism (3 attempts) before falling back to a safe default payload.
- 🔗 **Traceable Implementation**: [ReactiveOrchestrator.java](src/main/java/com/reactivelab/webclient/service/ReactiveOrchestrator.java) | [InventoryController.java](src/main/java/com/reactivelab/webclient/controller/InventoryController.java) | [Test Suite](src/test/java/com/reactivelab/webclient/service/ReactiveOrchestratorTest.java)

### Scenario 5: Stream Consumption (SSE)
Establish a persistent connection to a streaming server-sent events (SSE) channel. Filter out internal `HEARTBEAT` signals on the fly, propagating events down to the subscriber.
- 🔗 **Traceable Implementation**: [ReactiveOrchestrator.java](src/main/java/com/reactivelab/webclient/service/ReactiveOrchestrator.java) | [EventController.java](src/main/java/com/reactivelab/webclient/controller/EventController.java) | [GlobalEvent.java](src/main/java/com/reactivelab/webclient/model/GlobalEvent.java) | [Test Suite](src/test/java/com/reactivelab/webclient/service/ReactiveOrchestratorTest.java)

### Scenario 6: Advanced Body Control
Use the low-level `exchangeToMono()` API to inspect HTTP headers. Programmatically determine whether to parse the body or discard it using `releaseBody()` to prevent crippling connection leaks.
- 🔗 **Traceable Implementation**: [ReactiveOrchestrator.java](src/main/java/com/reactivelab/webclient/service/ReactiveOrchestrator.java) | [SecureDataController.java](src/main/java/com/reactivelab/webclient/controller/SecureDataController.java) | [Test Suite](src/test/java/com/reactivelab/webclient/service/ReactiveOrchestratorTest.java)

---

## 📖 Command Dissection

| API Constructor / Operator | Target Behavior | Educational Rationale |
| :--- | :--- | :--- |
| `WebClient.builder()` | Configures foundational client settings (Base URL, default headers, connection pool settings). | Prevents ad-hoc instantiation and facilitates standard Spring dependency injection. |
| `.retrieve()` | Triggers standard HTTP request execution. | Automatically handles body consumption and safely releases the connection, avoiding memory leaks. |
| `.exchangeToMono(response -> ...)` | Yields the complete `ClientResponse` structure (status, headers) for manual request parsing. | High-risk: **Requires manual body release** (`response.releaseBody()`) or full body consumption to avoid connection pool exhaustion. |
| `Mono.zip(MonoA, MonoB)` | Combines two independent asynchronous publishers into a single tuple. | Core operator for **parallel non-blocking orchestration**, avoiding thread serialization. |
| `.timeout(Duration)` | Fires a `TimeoutException` if the publisher emits no signal within the timeframe. | Essential resilience guard ensuring slow services don't pool-starve the gateway. |

---

## 🧠 Self-Assessment

<details>
<summary>1. In a Thread-per-Request model vs. WebClient, what happens to the execution thread while waiting for a slow API response?</summary>
<p>
In the traditional <b>Thread-per-Request</b> model (like Tomcat and <code>RestTemplate</code>), the request thread is blocked in a native socket read operation. It is "held hostage" and is completely unusable for other tasks. 

In contrast, <b>WebClient</b> delegates the HTTP socket read/write tasks to Netty. Once the request is sent, the event loop thread (<code>reactor-http-nio-X</code>) is <b>immediately released</b> to process other traffic. When the operating system signals that the downstream response is available, Netty non-blockingly resumes the pipeline.
</p>
</details>

<details>
<summary>2. Why can't we block Netty's <code>reactor-http-nio-X</code> event loop threads?</summary>
<p>
Netty's event loop operates with a very small pool of threads (typically matching CPU cores). If you block one event loop thread (e.g., with <code>Thread.sleep()</code>, synchronous DB queries, or blocking file I/O), you are freezing 25% to 100% of your application's request processing engine, immediately stalling hundreds of concurrent user requests.
</p>
</details>

<details>
<summary>3. What is Loom Carrier Thread Pinning, and how does WebClient avoid it?</summary>
<p>
In Java 21+ Virtual Threads (Project Loom), a virtual thread is scheduled onto a platform thread called a <b>Carrier Thread</b>. If a virtual thread enters a <code>synchronized</code> block or executes a native method and then blocks on I/O, the underlying Carrier Thread is <b>pinned</b> and cannot be released. This stalls the JVM ForkJoinPool scheduler. 

<code>WebClient</code> avoids pinning entirely because it never blocks or yields executing threads; its Netty network channel reads and writes are purely event-driven, operating completely free of synchronized blocking wait structures.
</p>
</details>

<details>
<summary>4. What is the root cause of a <code>PoolAcquireTimeoutException</code> in WebClient applications?</summary>
<p>
This exception occurs when all persistent TCP connections managed by WebClient's <code>ConnectionProvider</code> are in use, and new requests in the acquire queue time out waiting for a connection. This is commonly caused by <b>connection leaks</b> resulting from using <code>exchangeToMono()</code> without either consuming the body (e.g., <code>.bodyToMono()</code>) or explicitly discarding it (<code>.releaseBody()</code>).
</p>
</details>

---

## ❓ Troubleshooting

- **`PoolAcquireTimeoutException`**: Verify that all `exchangeToMono()` invocations are calling `.releaseBody()` on all branches where the response body is ignored.
- **`BlockHoundRuntimeError`**: You are attempting to run a blocking method (like filesystem read or legacy logging) inside a reactive Event Loop thread. Wrap the blocking logic using `publishOn(Schedulers.boundedElastic())` or refactor it into an asynchronous alternative.

---

## 🧹 Cleanup
The mock environments are automatically garbage-collected and shutdown gracefully on test termination. No manual docker cleanup is required for this laboratory.
