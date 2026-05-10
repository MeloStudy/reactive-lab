# Tasks: PROJ-001: The Reactive GitHub Insights Engine

## Phase 1: Setup & Data
- [ ] T001 Initialize Maven project in `projects/001-github-insights`.
- [ ] T002 Add Jackson and Reactor dependencies.
- [ ] T003 Prepare a trimmed 50,000-line sample file from GHArchive for dev testing.

## Phase 2: Core Ingestion Pipeline
- [ ] T004 Implement `Flux` file reader with proper resource cleanup.
- [ ] T005 Implement Jackson-based JSON parsing.
- [ ] T006 Add thread-offloading via `publishOn` for parsing logic.
- [ ] T007 Implement `onErrorContinue` logic for malformed JSON.

## Phase 3: Analytics Logic
- [ ] T008 Implement `PushEvent` commit count extraction.
- [ ] T009 Implement `WatchEvent` repository star tracking.
- [ ] T010 Implement Language extraction from `PullRequestEvent` payloads.
- [ ] T011 Aggregate star counts and Language occurrences to find Top 5 of each.
- [ ] T012 Calculate final execution metrics (Total events, duration).
- [ ] T013 Implement the "Executive Summary" console output.

## Phase 4: Security Enrichment (Aggregation)
- [ ] T017 Create `security-database.json` mock file.
- [ ] T018 Implement `SecurityService` for reactive data lookup.
- [ ] T019 Implement enrichment flow using multi-source operators (zip/flatMap).
- [ ] T020 Update reporter to include security insights.

## Phase 5: Verification & Audit
- [ ] T014 Run stress test (50,000 events).
- [ ] T015 Verify memory constraints (Heap < 128MB).
- [ ] T016 Final pedagogical audit of the solution.
