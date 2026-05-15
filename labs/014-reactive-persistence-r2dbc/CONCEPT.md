# CONCEPT: Reactive Persistence with R2DBC

Relational databases have traditionally been accessed via JDBC, which is a blocking API. In a reactive system, blocking a thread while waiting for a database response is unacceptable. **R2DBC** (Reactive Relational Database Connectivity) was created to bring non-blocking database access to relational databases.

## 1. Why R2DBC? (The Blocking Problem)
Traditional relational database access via **JDBC** (Java Database Connectivity) is inherently synchronous. In a standard JDBC-based application (using Spring Data JPA or plain JDBC):
- **Thread-per-Connection**: Every database connection requires a dedicated OS thread.
- **Blocking I/O**: When a thread executes a query, it blocks and enters a "waiting" state until the database returns the result.
- **Resource Exhaustion**: To handle high concurrency, you need a large thread pool (e.g., **HikariCP**). However, context switching between thousands of threads becomes a bottleneck, and memory consumption (1MB per thread stack) limits scalability.

**R2DBC** (Reactive Relational Database Connectivity) breaks this model by implementing a non-blocking wire protocol.

## 2. The R2DBC Mechanism (Netty & TCP)
Instead of blocking threads, R2DBC drivers (like `r2dbc-postgresql`) are built on top of **Netty**.
- **Asynchronous TCP**: Communication with the database happens over asynchronous TCP sockets.
- **Event Loop Multiplexing**: A single Event Loop thread can manage hundreds of concurrent database connections. When a query is sent, the thread is immediately freed to handle other tasks.
- **Callbacks/Signals**: When the database response arrives, the Event Loop receives a signal and pushes the data into the reactive stream (`Flux` or `Mono`).
- **ConnectionFactory**: Unlike a JDBC `DataSource`, the R2DBC `ConnectionFactory` provides a reactive entry point that doesn't rely on `ThreadLocal` or blocking pools.

## 3. The Death of JPA & Hibernate in Reactive
A common misconception is that you can use **JPA** or **Hibernate** with R2DBC. **You cannot.**
- **Blocking by Design**: JPA is a blocking specification. Hibernate's core mechanisms (Lazy Loading, Dirty Checking, First-level Cache) rely heavily on `ThreadLocal` and synchronous execution.
- **Lazy Loading**: If you access a lazy-loaded collection outside a transaction, Hibernate blocks to fetch it. This is impossible in a non-blocking pipeline where no thread can be held hostage.
- **The Alternative**: **Spring Data R2DBC**. It is NOT a full ORM. It is a "Basic" mapping layer that provides:
    - **ReactiveCrudRepository**: Simple mapping from rows to POJOs without the overhead of a persistence context.
    - **No Lazy Loading**: All relationships must be handled explicitly (e.g., via joins in `DatabaseClient`).
    - **Transparency over Magic**: You have more control over the SQL, but you lose the "automagic" features of Hibernate. This is a deliberate trade-off for high-performance, predictable reactive systems.

## 4. Spring Data R2DBC
Spring provides a familiar programming model with:
- **ReactiveCrudRepository**: Provides standard CRUD methods returning `Mono` and `Flux`.
- **DatabaseClient**: A non-blocking client for executing manual SQL queries.
- **R2DBC Entity Template**: A lower-level API for programmatic database access.

## 5. Reactive Transactions
Transactions in WebFlux are different. Since there is no `ThreadLocal` storage for the transaction state, Spring uses the **Reactor Context** to propagate the transaction across the pipeline. 
When you annotate a method with `@Transactional`, the transaction is bound to the reactive stream. If the stream is cancelled or emits an error, the transaction is automatically rolled back.

## 6. PostgreSQL JSONB Support
One of the advantages of using PostgreSQL with R2DBC is the ability to handle **JSONB** columns without blocking. This allows you to combine the power of relational data with the flexibility of document-based data in a single, non-blocking pipeline.

## 7. Observability with R2DBC Proxy
Since we don't have the traditional JDBC interceptors, we use `r2dbc-proxy`. This allows us to wrap the `ConnectionFactory` and listen to query execution events, providing visibility into the generated SQL and execution times without introducing blocking calls.

## 8. Evolution: R2DBC vs. JDBC + Virtual Threads (Java 21+)
With the introduction of **Project Loom (Virtual Threads)**, the debate around non-blocking database access has evolved. Since Virtual Threads make blocking calls extremely cheap (millions of threads can exist simultaneously without consuming significant RAM), many argue that standard JDBC over Virtual Threads is sufficient.

- **JDBC + Virtual Threads**: Excellent for simple request-response flows. The virtual thread blocks during the DB call, but the underlying OS carrier thread is freed. It drastically simplifies code readability.
- **When to use R2DBC**: R2DBC remains the superior choice for **streaming large datasets** directly from the database to the client via WebFlux (SSE or NDJSON). R2DBC inherently supports reactive backpressure, meaning if the HTTP client reads data slowly, R2DBC will automatically slow down the database retrieval, preventing memory `OutOfMemoryError`s. JDBC, even with Virtual Threads, pulls the entire result set into memory or blocks unpredictably during cursor iterations.
