# Implementation Plan: LAB-015: Reactive Persistence with MongoDB [AUDITED]

**Branch**: `015-reactive-persistence-mongodb` | **Date**: 2026-05-04
**Input**: Specification from `docs/specs/015-reactive-persistence-mongodb/spec.md`

## Summary

This lab focuses on the unique "Streaming" capabilities of NoSQL databases. Unlike R2DBC, MongoDB was designed for event-driven architectures, offering native support for infinite streams (tailable cursors) and event watching (change streams).

## Phase 1: Infrastructure & Document Setup
1. **Scaffold**: Create `labs/015-reactive-persistence-mongodb/`.
2. **Workspace Registration**: Add the lab to the root `pom.xml`.
3. **Database Configuration**: Configure MongoDB Reactive with `testcontainers`.
4. **Schema/Collection Setup**: Programmatically ensure Capped Collections exist upon startup.

## Phase 2: Streaming & Cursors
1. **Instructional Path**:
   - Creating `@Document` entities.
   - Implementing `Tailable` queries in `ReactiveMongoRepository`.
   - Building a Server-Sent Events (SSE) log streamer.
2. **Validation**: Test verifying that the stream remains open after initial consumption.

## Phase 3: Change Streams & GridFS
1. **Instructional Path**:
   - Setting up `ReactiveMongoTemplate` for Change Stream watching.
   - Using `ReactiveGridFsTemplate` for non-blocking file I/O (both Upload and Download).
   - Implementing Aggregation pipelines.
   - Simulating and handling Backpressure using `.onBackpressureDrop()`.
2. **Validation**: Tests for real-time price change notifications, backpressure handling, and full file integrity (upload + download).

## Phase 4: Documentation & Dissection
1. **CONCEPT.md**: Explain the "Push" vs "Pull" models in database streaming, the `ReactiveMongoClient` hierarchy, and Backpressure implications.
2. **README.md**: Command Dissections for `@Tailable`, `watch()`, GridFS operations, and Backpressure handling.
3. **Docker**: Provide `docker-compose.yml` for local MongoDB Replica Set (required for Change Streams).

## Phase 5: Constitution v0.2.7 Refinement
1. **Instructional Path**:
   - Refactor codebase into `config`, `controller`, `model`, `repository`, and `service` packages for cleaner modularity.
   - Add Traceable Implementation links to `README.md` scenarios (reflecting the new packages).
   - Inject the missing "Self-Assessment" section with collapsible `<details>` blocks.
   - Update `CONCEPT.md` with Reactive MongoDB vs Virtual Threads (Modern Technology Assimilation).
2. **Validation**: Verify that relative links point to valid Java source files and tests. Ensure AssertJ uses `containsEntry` where applicable.

## Decisions
- **Initialization**: We will use **Programmatic Initialization** via `ReactiveMongoTemplate` and `ApplicationRunner` to show explicit signal handling for Capped Collection creation. Mongock will be mentioned in `CONCEPT.md` as a production alternative.
- **Scope**: The lab will remain focused exclusively on **MongoDB** to master its advanced streaming capabilities (*Tailable Cursors*, *Change Streams*, *GridFS*).

## Constitution Compliance Check
- [x] No `.sh` scripts.
- [x] Explicit mentions of Signal propagation.
- [x] **Infrastructure**: Replica Set configuration for MongoDB is mandatory for Change Streams.
