# Mini-Project 2: The Polyglot Reactive Store (ReactiveMart) 🚀

Welcome to the second Mini-Project! This challenge focuses on **Enterprise Reactive Orchestration** in a hybrid environment where modern Java services must interact with legacy Python systems.

## 🎯 The Mission
You are tasked with building the order processing engine for **ReactiveMart**, a high-concurrency flash-sale platform. The catch? While the order system is modern (WebFlux), the inventory system is a legacy Python/Flask application that is intentionally slow and prone to timeouts.

## 🏗️ Architecture
- **`order-service` (Java 21 / Spring WebFlux)**: 
  - Uses **R2DBC (H2)** for Order persistence.
  - Uses **Reactive MongoDB** for Product Metadata.
  - Orchestrates calls via `WebClient`.
- **`inventory-service` (Python 3.12 / Flask)**: 
  - Simulates a legacy blocking service.
  - Uses **SQLite** for stock tracking.
  - Artificial latency introduced to test resilience.

## ⚙️ Prerequisites
- Docker or Podman
- Docker Compose or Podman Compose
- Java 21+ and Maven (for local development)
- Python 3.12+ (for local development)

## 🚀 How to Run (Containerized)

> [!IMPORTANT]
> All commands must be executed from the project root directory: `projects/002-reactive-store`.

### 1. Build and Start the Ecosystem
Run the following command to build and start all services:
```bash
docker-compose up --build
```
*Note: Works identically with Podman (or `podman-compose`).*

### 2. Verify Services
- **Order Service**: `http://localhost:8080`
- **Inventory Service**: `http://localhost:5000`
- **MongoDB**: `localhost:27017`

## 🧪 Scenarios to Test

### 1. The Happy Path
Place an order for a fast product:
```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -H "X-Correlation-ID: MY-TRACE-001" \
  -d @samples/order-happy.json
```
**Expectation**: Order completed in < 100ms. Check logs in both containers to see `MY-TRACE-001`.

### 2. The Resilience Test (Legacy Slowness)
Place an order for `PROD-004` (The slow product):
```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d @samples/order-timeout.json
```
**Expectation**: The Python service will delay 1-2s. Since the Java service has an **800ms timeout**, the order should fail gracefully or trigger a fallback.

### 3. Management Operations (Full CRUD)
Explore the newly implemented CRUD operations:

**List all orders**:
```bash
curl http://localhost:8080/orders
```

**Get a specific order**:
```bash
curl http://localhost:8080/orders/1
```

**Update order status (Patch)**:
```bash
curl -X PATCH "http://localhost:8080/orders/1?status=SHIPPED"
```

**Delete/Cancel an order**:
```bash
curl -X DELETE http://localhost:8080/orders/1
```

### 4. Distributed Tracing Audit
Run several requests and check the logs:
```bash
docker-compose logs -f
```
Verify that the `X-Correlation-ID` generated in Java (or passed via header) is correctly logged by the Python service.

## 🛠️ Technical Constraints
- **Independent POM**: This project does not inherit from the root parent POM. It uses its own `pom.xml` with Spring Boot parent to emulate a real-world standalone microservice deployment.
- **Non-Blocking**: The Java service must never block. Validation via **BlockHound** is required.
- **Resilience**: Timeout on external calls is mandatory.
- **Context Propagation**: Correlation ID must be preserved through the `WebClient` call.

## 🧠 Knowledge Check
<details>
<summary>1. Why do we use <code>Mono.deferContextual</code> instead of standard variables for the Correlation ID?</summary>
Reactor pipelines are asynchronous and can jump between threads. Standard ThreadLocals do not work. <code>Context</code> is the reactive way to propagate state along the pipeline.
</details>

<details>
<summary>2. What happens if the Inventory Service takes 900ms and our timeout is 800ms?</summary>
The <code>timeout</code> operator will emit a <code>TimeoutException</code>, which can be handled by <code>onErrorResume</code> to provide a fallback or return an error response.
</details>

<details>
<summary>3. Why use R2DBC for the database instead of JPA/JDBC?</summary>
Standard JDBC is blocking. R2DBC provides a non-blocking API for relational databases, allowing the entire application to remain responsive under high load.
</details>
