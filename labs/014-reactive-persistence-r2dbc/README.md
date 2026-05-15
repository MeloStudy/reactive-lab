# LAB-014: Reactive Persistence with R2DBC

## Overview
In this laboratory, you will break the "Blocking DB" barrier. You will implement a complete persistence layer using PostgreSQL and R2DBC, exploring how to maintain transactional integrity and observability in a non-blocking environment.

## Learning Objectives
- Configure a PostgreSQL database using **Testcontainers**.
- Implement reactive repositories with **JSONB** support.
- Perform manual SQL operations using `DatabaseClient`.
- Validate **Reactive Transactions** and rollbacks.
- Audit SQL execution using **R2DBC Proxy**.

## Prerequisites
- **Docker** or **Podman** must be installed and running (for Testcontainers and manual deployment).

## Instructions

1.  **Scenario 1: The Infrastructure**
    Observe `PersistenceIntegrationTest.java`. Note how `PostgreSQLContainer` is used to spin up a real database for the tests.
    🔗 **Traceable Implementation**: [Test Suite](src/test/java/com/reactivelab/r2dbc/PersistenceIntegrationTest.java)
    
2.  **Scenario 2: JSONB Documents**
    Look at the `Product` entity. It contains a `Json` metadata field. In the tests, observe how we persist and retrieve JSON data from PostgreSQL.
    🔗 **Traceable Implementation**: [PersistenceModels.java](src/main/java/com/reactivelab/r2dbc/PersistenceModels.java) | [Test Suite](src/test/java/com/reactivelab/r2dbc/PersistenceIntegrationTest.java)

3.  **Scenario 3: Custom SQL**
    Check `ProductService.findExpensiveProducts`. Instead of a repository method, we use `DatabaseClient` to execute a manual query and map the results to the entity.
    🔗 **Traceable Implementation**: [PersistenceLogic.java](src/main/java/com/reactivelab/r2dbc/PersistenceLogic.java) | [Test Suite](src/test/java/com/reactivelab/r2dbc/PersistenceIntegrationTest.java)

4.  **Scenario 4: Transactional Rollback**
    Review `ProductService.purchaseProduct`. The method is marked with `@Transactional`. The test `scenario4_transactionalRollback` verifies that if an error occurs during the order creation, the stock decrement is rolled back.
    🔗 **Traceable Implementation**: [PersistenceLogic.java](src/main/java/com/reactivelab/r2dbc/PersistenceLogic.java) | [Test Suite](src/test/java/com/reactivelab/r2dbc/PersistenceIntegrationTest.java)

5.  **Scenario 5: SQL Auditing**
    Examine `R2dbcConfiguration`. We wrap the `ConnectionFactory` with `ProxyConnectionFactory`. Check the logs during test execution to see the intercepted SQL queries.
    🔗 **Traceable Implementation**: [PersistenceLogic.java](src/main/java/com/reactivelab/r2dbc/PersistenceLogic.java) | [Test Suite](src/test/java/com/reactivelab/r2dbc/PersistenceIntegrationTest.java)

## Command Dissections

### 1. `ReactiveCrudRepository`
```java
public interface ProductRepository extends ReactiveCrudRepository<Product, Long>
```
- **What**: The reactive counterpart to Spring Data JPA's `CrudRepository`.
- **Why**: Allows CRUD operations without blocking threads.

### 2. `DatabaseClient`
```java
databaseClient.sql("SELECT ...").bind("id", id).map(...).all()
```
- **What**: A fluent API for executing SQL.
- **Why**: Essential for complex queries, joins, or database-specific features not supported by the repository abstraction.

### 3. `@Transactional` (Reactive)
- **What**: Annotation to mark transactional boundaries.
- **Why**: In WebFlux, it works by propagating the transaction state through the Reactor Context instead of `ThreadLocal`.

## 🧠 Self-Assessment
<details>
<summary>1. How does R2DBC differ from JDBC in handling concurrent requests and threading?</summary>
JDBC blocks one OS thread per connection while waiting for the database response, requiring large thread pools (like HikariCP) and incurring context-switching overhead. R2DBC uses an <b>Event Loop</b> and asynchronous <b>TCP sockets (via Netty)</b>, allowing a single thread to multiplex thousands of concurrent database operations without blocking.
</details>

<details>
<summary>2. Why are JPA and Hibernate incompatible with R2DBC?</summary>
JPA and Hibernate are built on the JDBC specification, which is synchronous by design. They rely heavily on <b>ThreadLocal</b> for transaction state and <b>Blocking I/O</b> for features like Lazy Loading and Dirty Checking. In a reactive pipeline, blocking a thread to fetch a lazy relationship would crash the event loop.
</details>

<details>
<summary>3. What are the "Basic" alternatives to full ORMs in the Spring R2DBC stack?</summary>
Instead of a full persistence context with magic features, Spring Data R2DBC provides <b>ReactiveCrudRepository</b> for simple POJO mapping and <b>DatabaseClient</b> for executing complex SQL manually. It lacks lazy loading and auto-dirty-checking, favoring transparency and non-blocking execution over complex abstraction.
</details>

<details>
<summary>4. How does <code>@Transactional</code> work in a reactive WebFlux environment?</summary>
Instead of using <code>ThreadLocal</code> (which breaks across asynchronous boundaries), Spring relies on the <b>Reactor Context</b> to propagate the transaction state through the reactive pipeline, ensuring consistency even when signals jump between different threads.
</details>

## Running Locally (Development)

You can use the provided `docker-compose.yml` to start only the PostgreSQL instance:
```bash
# Using Docker
docker compose up postgres-lab -d

# Using Podman
podman-compose up postgres-lab -d
```

### 2. Run the Application
```bash
mvn spring-boot:run -pl labs/014-reactive-persistence-r2dbc
```
The application will connect to `localhost:5432` using the default credentials in `application.properties`.

---

## Docker Deployment (Full Stack)

To run the entire stack (Database + App) in containers:

### 1. Build the Jar
```bash
mvn clean package -pl labs/014-reactive-persistence-r2dbc -DskipTests
```

### 2. Start the Stack
```bash
# Using Docker
docker compose up --build

# Using Podman
podman-compose up --build
```
This will:
1. Start PostgreSQL and wait for it to be healthy.
2. Build the App image using the `Dockerfile`.
3. Start the App and connect it to the database container.

---

## Verification
Run the tests:
```bash
mvn test -pl labs/014-reactive-persistence-r2dbc
```
Observe the logs to see the R2DBC Proxy output!
