# LAB-011: Reactive Web with Spring WebFlux

Building high-performance, non-blocking web services with Project Reactor and Spring WebFlux.

## Overview
This laboratory demonstrates the transition from core Reactor concepts to building real-world web applications. You will explore both the **Annotated Controller** model and the **Functional Router** model, along with advanced streaming techniques like SSE and NDJSON.

## Key Scenarios

### 1. Annotated @RestController
- **Endpoint**: `GET /stocks/{symbol}`
- **Concept**: Familiar Spring MVC style but with non-blocking return types.
- **Example**: `curl -v http://localhost:8080/stocks/AAPL`

### 2. Functional Router & Handler
- **Endpoint**: `GET /functional/stocks/{symbol}`
- **Concept**: Programmatic routing with explicit `HandlerFunction` and `HandlerFilterFunction`.
- **Custom Header**: Every functional response includes an `X-Execution-Time` header added via a filter.
- **Example**: `curl -v http://localhost:8080/functional/stocks/MSFT`

### 3. Server-Sent Events (SSE) Stream
- **Endpoint**: `GET /stocks/{symbol}/stream`
- **Concept**: Pushing live data updates over a single HTTP connection.
- **Example**: `curl -N http://localhost:8080/stocks/AAPL/stream`

### 4. NDJSON Bulk Export
- **Endpoint**: `GET /stocks/bulk?count=100`
- **Concept**: Streaming large JSON datasets one element at a time (Newline Delimited JSON).
- **Example**: `curl -v http://localhost:8080/stocks/bulk?count=5`

### 5. Global Error Handling
- **Scenario**: Requesting a non-existent stock symbol.
- **Concept**: Translating reactive errors (`StockNotFoundException`) into HTTP status codes (`404 Not Found`).
- **Example**: `curl -v http://localhost:8080/stocks/UNKNOWN`

## How to Run
```bash
mvn spring-boot:run
```

## How to Test
```bash
mvn test
```

## Knowledge Check

<details>
<summary>1. Why does the Functional Router use ServerWebExchange to add headers instead of modifying the ServerResponse directly?</summary>
In the functional model, `ServerResponse` is often immutable or built within the handler. Using the `filter` with `ServerWebExchange` allows for cross-cutting concerns (like logging or security headers) to be applied to the final response regardless of how it was generated in the handler.
</details>

<details>
<summary>2. What happens to the memory usage when you request 1,000,000 bulk quotes via NDJSON vs. a standard JSON list?</summary>
With a standard JSON list, the server (and client) must buffer all 1,000,000 elements to form a valid JSON array `[...]`. This can cause OutOfMemory errors. With NDJSON, elements are streamed one by one, keeping memory usage constant and low.
</details>

<details>
<summary>3. How many threads does Netty use by default?</summary>
Netty defaults to `2 * number of available CPU cores`. This small pool is sufficient because no thread ever blocks.
</details>

<details>
<summary>4. Can Virtual Threads replace WebFlux entirely?</summary>
While Virtual Threads simplify imperative concurrency, WebFlux remains superior for scenarios requiring native backpressure, complex stream transformations, and real-time push protocols (SSE/WebSockets).
</details>
