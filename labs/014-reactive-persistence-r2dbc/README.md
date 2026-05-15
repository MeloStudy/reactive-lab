# LAB-014: Reactive Persistence with R2DBC

## Overview
In this laboratory, you will break the "Blocking DB" barrier. You will implement a complete persistence layer using PostgreSQL and R2DBC, exploring how to maintain transactional integrity and observability in a non-blocking environment.

## Learning Objectives
- Configure a PostgreSQL database using **Testcontainers**.
- Implement reactive repositories with **JSONB** support.
- Master the **Template Hierarchy**: `ReactiveCrudRepository`, `R2dbcEntityTemplate`, and `DatabaseClient`.
- Understand the **Basic ORM** approach vs. traditional blocking ORMs.
- Validate **Reactive Transactions** and rollbacks.
- Audit SQL execution using **R2DBC Proxy**.

## Prerequisites
- **Docker** or **Podman** must be installed and running (for Testcontainers and manual deployment).

## Instructions

1.  **Scenario 1: The Infrastructure**
    Observe `PersistenceIntegrationTest.java`. Note how `PostgreSQLContainer` is used to spin up a real database for the tests.
    🔗 **Traceable Implementation**: [PersistenceIntegrationTest.java](src/test/java/com/reactivelab/r2dbc/PersistenceIntegrationTest.java)
    
2.  **Scenario 2: JSONB Documents**
    Look at the `Product` entity. It contains a `Json` metadata field. In the tests, observe how we persist and retrieve JSON data from PostgreSQL.
    🔗 **Traceable Implementation**: [Product.java](src/main/java/com/reactivelab/r2dbc/model/Product.java) | [PersistenceIntegrationTest.java](src/test/java/com/reactivelab/r2dbc/PersistenceIntegrationTest.java)

3.  **Scenario 3: Advanced Templates (Client vs Template)**
    Check `ProductService`. 
    - `searchByName` uses `DatabaseClient` for raw SQL flexibility.
    - `searchByPriceRange` uses `R2dbcEntityTemplate` for type-safe programmatic criteria.
    🔗 **Traceable Implementation**: [ProductService.java](src/main/java/com/reactivelab/r2dbc/service/ProductService.java) | [PersistenceIntegrationTest.java](src/test/java/com/reactivelab/r2dbc/PersistenceIntegrationTest.java)

4.  **Scenario 4: Transactional Rollback**
    Review `ProductService.purchaseProduct`. The method is marked with `@Transactional`. The test `scenario4_transactionalRollback` verifies that if an error occurs during the order creation, the stock decrement is rolled back.
    🔗 **Traceable Implementation**: [ProductService.java](src/main/java/com/reactivelab/r2dbc/service/ProductService.java) | [PersistenceIntegrationTest.java](src/test/java/com/reactivelab/r2dbc/PersistenceIntegrationTest.java)

5.  **Scenario 5: SQL Auditing**
    Examine `R2dbcConfiguration`. We wrap the `ConnectionFactory` with `ProxyConnectionFactory`. Check the logs during test execution to see the intercepted SQL queries.
    🔗 **Traceable Implementation**: [R2dbcConfiguration.java](src/main/java/com/reactivelab/r2dbc/config/R2dbcConfiguration.java) | [PersistenceIntegrationTest.java](src/test/java/com/reactivelab/r2dbc/PersistenceIntegrationTest.java)

## Command Dissections

### 1. `ReactiveCrudRepository`
```java
public interface ProductRepository extends ReactiveCrudRepository<Product, Long>
```
- **What**: The reactive counterpart to Spring Data JPA's `CrudRepository`.
- **Why**: Allows CRUD operations without blocking threads.

### 2. `R2dbcEntityTemplate`
```java
entityTemplate.select(Product.class)
    .matching(Query.query(Criteria.where("price").between(min, max)))
    .all();
```
- **What**: A mid-level abstraction for programmatic query construction.
- **Why**: Type-safe criteria search without writing raw SQL strings.

### 3. `DatabaseClient`
```java
databaseClient.sql("SELECT ...").bind("id", id).map(...).all()
```
- **What**: A fluent API for executing raw SQL.
- **Why**: Essential for complex joins, PostgreSQL-specific features (JSONB operators), or high-performance bulk operations.

### 4. `@Transactional` (Reactive)
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
<summary>3. What is a "Basic ORM" and how does it compare to Hibernate?</summary>
A "Basic ORM" like Spring Data R2DBC provides row-to-POJO mapping but lacks a persistence context. It has <b>no session</b> (no auto-dirty-checking), <b>no lazy loading</b>, and <b>no proxying</b>. You gain performance and predictability at the cost of the "automagic" features found in full ORMs.
</details>

<details>
<summary>4. When should I use R2dbcEntityTemplate instead of DatabaseClient?</summary>
Use <b>R2dbcEntityTemplate</b> for dynamic, type-safe queries where you want to avoid raw SQL strings. Use <b>DatabaseClient</b> when you need the full power of SQL, such as complex joins, native functions, or specialized operators (like JSONB query operators) that aren't easily expressed in the Criteria API.
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
