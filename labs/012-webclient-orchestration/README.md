# LAB-012: WebClient: Orchestrating Downstream Services

Welcome to Lab 12! In this module, you will learn how to build a high-performance reactive gateway that orchestrates multiple external microservices using `WebClient`.

## 🎯 Learning Objectives
- Configure a non-blocking `WebClient`.
- Master the `retrieve()` API for simple data fetching.
- Orchestrate parallel and sequential service calls.
- Implement resilience patterns (Timeouts, Retries, Fallbacks).
- Efficiently consume streaming responses.

## 🚀 Getting Started

### 1. Build the project
```bash
mvn clean install -pl labs/012-webclient-orchestration
```

### 2. Run the validation tests
```bash
mvn test -pl labs/012-webclient-orchestration
```

## 🛠️ Hands-On: The Orchestrator

### Scenario 1: The Simple Fetcher
Your goal is to fetch a user profile from a remote service.

> **Command Dissection: WebClient.retrieve()**
> - `.get()`: Specifies the HTTP method.
> - `.uri("/users/{id}", id)`: Sets the path and variables.
> - `.retrieve()`: The easiest way to get the response body.
> - `.bodyToMono(User.class)`: Decodes the response body into a specific type.

### Scenario 2: Parallel Orchestration
Use `Mono.zip` to fetch User data and Order data simultaneously. This is the "Magic of Reactive" where the total time is only as slow as the slowest single call.

### Scenario 3: Dependent Calls
Sometimes you need data from call A to perform call B. Use `flatMap` to chain these operations without blocking.

### Scenario 4: The Resilient Client
External services are unreliable. Implement a 2-second timeout and an exponential backoff retry strategy.

> **Command Dissection: retryWhen()**
> - `Retry.backoff(maxAttempts, minBackoff)`: Creates a strategy that waits longer between each failure.
> - `filter(predicate)`: Only retries for specific types of errors.

## 🧠 Self-Assessment
<details>
<summary>1. Why is <code>retrieve()</code> generally safer than <code>exchange()</code>?</summary>
Because <code>retrieve()</code> handles the consumption of the response body for you, whereas <code>exchange()</code> (or <code>exchangeToMono</code>) makes YOU responsible for it. If you forget to consume the body in <code>exchange()</code>, you leak memory and connection slots.
</details>

<details>
<summary>2. If I have 3 independent service calls taking 100ms, 200ms, and 300ms, how long will <code>Mono.zip</code> take?</summary>
Approximately 300ms (the time of the slowest call), because they all start in parallel.
</details>

<details>
<summary>3. What happens if I block inside a WebClient pipeline?</summary>
You block the Event Loop thread, which can severely degrade the performance of the entire application, as the same thread might be handling many other concurrent requests.
</details>

## 🧹 Cleanup
The lab uses `MockWebServer` which shuts down automatically after tests. No manual cleanup is required.
