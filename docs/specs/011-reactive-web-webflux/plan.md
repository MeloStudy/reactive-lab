# Implementation Plan: LAB-011: Reactive Web with Spring WebFlux [READY]

**Branch**: `011-reactive-web-webflux` | **Date**: 2026-05-04
**Input**: Specification from `/specs/011-reactive-web-webflux/spec.md`

## Summary

This lab transitions from pure Reactor core concepts to real-world web application development. It focuses on the architectural shift required to handle HTTP requests in a non-blocking manner using Spring WebFlux.

## Phase 1: Spring Boot Infrastructure
1. **Initialize**: Use `spring-boot-starter-webflux`.
2. **Monorepo Setup**: Register the module in root `pom.xml`.
3. **Constitution Check**: Ensure `WebTestClient` is used for testing, not MockMvc.

## Phase 2: Annotated vs Functional Models
1. Implement a `StockService` returning reactive types.
2. Implement `@RestController`.
3. Implement `RouterFunction` & `HandlerFunction`.
4. Establish TDD validation for both.

## Phase 3: Streaming (SSE & NDJSON)
1. Create a `TickerController` for SSE.
2. Create an `ExportController` for NDJSON.
3. Demonstrate backpressure in the browser/client.

## Phase 4: Documentation
1. Draft `CONCEPT.md`: Focus on the Netty Event Loop and the non-blocking I/O model.
2. Draft `README.md`: Instructions for running the Spring Boot app and testing endpoints with `curl`.

## Open Questions
- None. (Comprehensive integration of Filters and Error Handling confirmed).
