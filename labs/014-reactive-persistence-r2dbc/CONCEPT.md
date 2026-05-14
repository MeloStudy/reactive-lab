# CONCEPT: Reactive Persistence with R2DBC

Relational databases have traditionally been accessed via JDBC, which is a blocking API. In a reactive system, blocking a thread while waiting for a database response is unacceptable. **R2DBC** (Reactive Relational Database Connectivity) was created to bring non-blocking database access to relational databases.

## 1. Why R2DBC?
In a standard JDBC application, if you have 100 concurrent requests, you need 100 threads, most of which are idle waiting for the DB. With R2DBC, a small number of threads can handle thousands of concurrent database operations by using asynchronous I/O.

## 2. The R2DBC Stack
- **Driver**: Specific to the DB (e.g., `r2dbc-postgresql`).
- **ConnectionFactory**: The reactive equivalent of a `DataSource`.
- **R2DBC Pool**: Since R2DBC doesn't use the standard JDBC connection pools (like HikariCP), we use `r2dbc-pool`.

## 3. Spring Data R2DBC
Spring provides a familiar programming model with:
- **ReactiveCrudRepository**: Provides standard CRUD methods returning `Mono` and `Flux`.
- **DatabaseClient**: A non-blocking client for executing manual SQL queries.
- **R2DBC Entity Template**: A lower-level API for programmatic database access.

## 4. Reactive Transactions
Transactions in WebFlux are different. Since there is no `ThreadLocal` storage for the transaction state, Spring uses the **Reactor Context** to propagate the transaction across the pipeline. 
When you annotate a method with `@Transactional`, the transaction is bound to the reactive stream. If the stream is cancelled or emits an error, the transaction is automatically rolled back.

## 5. PostgreSQL JSONB Support
One of the advantages of using PostgreSQL with R2DBC is the ability to handle **JSONB** columns without blocking. This allows you to combine the power of relational data with the flexibility of document-based data in a single, non-blocking pipeline.

## 6. Observability with R2DBC Proxy
Since we don't have the traditional JDBC interceptors, we use `r2dbc-proxy`. This allows us to wrap the `ConnectionFactory` and listen to query execution events, providing visibility into the generated SQL and execution times without introducing blocking calls.

## 7. Evolution: R2DBC vs. JDBC + Virtual Threads (Java 21+)
With the introduction of **Project Loom (Virtual Threads)**, the debate around non-blocking database access has evolved. Since Virtual Threads make blocking calls extremely cheap (millions of threads can exist simultaneously without consuming significant RAM), many argue that standard JDBC over Virtual Threads is sufficient.

- **JDBC + Virtual Threads**: Excellent for simple request-response flows. The virtual thread blocks during the DB call, but the underlying OS carrier thread is freed. It drastically simplifies code readability.
- **When to use R2DBC**: R2DBC remains the superior choice for **streaming large datasets** directly from the database to the client via WebFlux (SSE or NDJSON). R2DBC inherently supports reactive backpressure, meaning if the HTTP client reads data slowly, R2DBC will automatically slow down the database retrieval, preventing memory `OutOfMemoryError`s. JDBC, even with Virtual Threads, pulls the entire result set into memory or blocks unpredictably during cursor iterations.
