# LAB-019: Modern Observability (Micrometer Observation)

Welcome to the Modern Observability lab. In this lab, you will configure and validate how Micrometer Observation unifies metrics and tracing in a Spring WebFlux application, without blocking the event loop.

## Architecture
You will run a Spring Boot WebFlux application alongside Prometheus (for Metrics) and Zipkin (for Distributed Tracing).

## Self-Assessment
<details>
<summary>Why can't we use standard MDC (ThreadLocal) in WebFlux for logging Trace IDs?</summary>
In a reactive application, an HTTP request's pipeline may switch threads multiple times (e.g., via `publishOn`). `ThreadLocal` is bound to the thread, so the context would be lost upon switching. Reactor provides a `Context` tied to the subscription to propagate this information safely across threads.
</details>

## Execution Steps

### 1. Launching the Observability Infrastructure
We will use Docker Compose to spin up Prometheus and Zipkin.

```bash
docker-compose up -d
# Alternatively, if using podman:
# podman-compose up -d
```

> **Command Dissection: `docker-compose up -d`**
> - `up`: Builds, (re)creates, starts, and attaches to containers for a service.
> - `-d`: Detached mode. Run containers in the background.

Check that Zipkin is available at `http://localhost:9411` and Prometheus at `http://localhost:9090`.

### 2. Scenario 1: WebClient and WebFlux Auto-Instrumentation
Spring Boot 3 + Micrometer automatically instrument WebFlux endpoints and WebClient calls. 

**Implementation:** [labs/019-modern-observability-micrometer/src/main/java/com/reactivelab/observability/ObservabilityService.java](../../labs/019-modern-observability-micrometer/src/main/java/com/reactivelab/observability/ObservabilityService.java)
**Test Validation:** [labs/019-modern-observability-micrometer/src/test/java/com/reactivelab/observability/Scenario1Test.java](../../labs/019-modern-observability-micrometer/src/test/java/com/reactivelab/observability/Scenario1Test.java)

We will validate this through automated tests using `TestObservationRegistry`, which intercepts observations in memory instead of sending them over the network.

Run the test for Scenario 1:
```bash
mvn test -Dtest=Scenario1Test
```

> **Command Dissection: `mvn test`**
> - `mvn test`: Executes the test phase of the Maven lifecycle natively.
> - `-Dtest=...`: Executes only the specified test class.

This test starts a `MockWebServer`, triggers an endpoint that uses `WebClient`, and asserts that an observation named `http.client.requests` was correctly registered in the context.

### 3. Scenario 2: Custom Observations and Tags
Sometimes we need to instrument business logic manually with high and low cardinality tags.

**Test Validation:** [labs/019-modern-observability-micrometer/src/test/java/com/reactivelab/observability/Scenario2Test.java](../../labs/019-modern-observability-micrometer/src/test/java/com/reactivelab/observability/Scenario2Test.java)

Run the test for Scenario 2:
```bash
mvn test -Dtest=Scenario2Test
```

This verifies that the custom `Observation` on our reactive pipeline correctly added the low cardinality tag `process.type=user-process` and the high cardinality tag `user.id`. 

### 4. Visual Validation (Optional Learner Journey)
1. Start the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```
   > **Command Dissection: `mvn spring-boot:run`**
   > - Bootstraps the embedded server (Netty) and runs the application interactively.
2. Trigger the endpoint in another terminal:
   ```bash
   curl "http://localhost:8080/api/call-downstream?url=https://httpbin.org/get"
   ```
3. Open **Zipkin** (`http://localhost:9411`) and you will see the trace traversing from the WebFlux controller to the WebClient HTTP call.
4. Open **Prometheus** (`http://localhost:9090`) and query `http_server_requests_seconds_count` or `http_client_requests_seconds_count`.

## Atomic Cleanup
When you are done, tear down the infrastructure to free resources.

```bash
docker-compose down -v --remove-orphans
# Alternatively, if using podman:
# podman-compose down -v --remove-orphans
```
