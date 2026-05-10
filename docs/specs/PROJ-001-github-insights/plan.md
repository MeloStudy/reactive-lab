# Implementation Plan: PROJ-001: The Reactive GitHub Insights Engine

**Branch**: `proj-001-github-insights` | **Date**: 2026-05-03
**Input**: Specification from `/docs/specs/PROJ-001-github-insights/spec.md`

## Phase 1: Infrastructure & Model
1. Create Maven project under `projects/001-github-insights`.
2. Add dependencies: `reactor-core`, `jackson-databind`, `reactor-test`, `assertj`.
3. Create Data Models: `GitHubEvent`, `Payload`, `Actor`.

## Phase 2: Reactive Ingestion (I/O Layer)
1. Implement `EventReader`: Uses `Files.lines()` wrapped in `Flux.using` or `Flux.fromStream`.
2. Implement `JSONParser`: Uses Jackson to map strings to `GitHubEvent`.
3. **Strategy**: Use `publishOn(Schedulers.parallel())` after the reader to parallelize parsing.

## Phase 3: Domain Logic (Transformation Layer)
1. Filter events: `filter(event -> "PushEvent".equals(event.getType()) || "WatchEvent".equals(event.getType()) || "PullRequestEvent".equals(event.getType()))`.
2. Extract metrics: `map` to a simplified internal `Metric` record.
3. Handle errors: Use `onErrorContinue` to log and skip invalid JSON lines.

## Phase 4: Aggregation (Reduction Layer)
1. Use `groupBy` or `collect` patterns for Star counting and Language ranking.
   - **Language Ranking**: Count occurrences of languages found in `PullRequestEvent` payloads.

## Phase 4.5: Data Enrichment (Multi-Source Aggregation)
1.  **Security Database**: Implement a secondary reactive source that reads `security-database.json`.
2.  **Lookup Logic**: Use `zip` or `flatMap` to enrich the Top 5 Repositories with their security vulnerability count.
3.  **Result Model**: Create a `SecurityProfile` to hold the merged data.

## Phase 5: Delivery & Reporting
1. Implement `SummaryReporter`: Collects total count, error count, and top 5 list.
2. Formulate the "Executive Summary" console output.

## Phase 6: Validation
1. Stress test: Run against a 50k line trimmed sample.
2. Memory test: Run with `-Xmx128m` and monitor for `OutOfMemoryError`.
