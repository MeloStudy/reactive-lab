# Project Specification: PROJ-001: The Reactive GitHub Insights Engine

**Feature Branch**: `proj-001-github-insights`
**Created**: 2026-05-03
**Status**: DRAFT
**Syllabus Section**: Mini-Project 1 (Integration of Level 1 Foundations)

## Project Alignment *(mandatory)*

- **Concept**: Integration of transformation, filtering, error handling, and concurrency on a real-world high-volume dataset.
- **Prerequisites**: LAB-001 through LAB-008.
- **Goal**: Build a memory-efficient processing engine for GitHub events using Project Reactor.

## Dataset Details *(mandatory)*

- **Source**: [GHArchive.org](https://www.gharchive.org/)
- **Format**: JSON (NewLine Delimited).
- **Volume**: Moderate (~50MB or ~50,000 events). A pre-processing "trimming" step is required to extract a representative sample from a full GHArchive file.

## Business Requirements *(mandatory)*

### Scenario: The Trending Repo Finder
An open-source analytics firm needs to identify trending repositories and active contributors without using expensive big-data infrastructure.

### Functional Requirements
1.  **Non-blocking Ingestion**: Read large JSON files line-by-line without loading the entire file into memory.
2.  **Event Filtering**: Process `PushEvent` (Commits), `WatchEvent` (Stars), and `PullRequestEvent` (PRs).
3.  **Data Extraction**:
    - Extract repository name and commit counts from `PushEvent`.
    - Extract repository name and actor login from `WatchEvent`.
    - Extract repository name and **Programming Language** from `PullRequestEvent`.
4.  **Analytics**:
    - Identify the **Top 5 Repositories** by Star count (WatchEvent) in the dataset.
    - Identify the **Top 5 Programming Languages**: Aggregate the occurrences of languages in `PullRequestEvent` and find the most active ones.
5.  **Resilience**: Skip malformed JSON lines and log the error count without stopping the pipeline.

## Technical Constraints *(mandatory)*

- **TR-001**: **Memory Limit**: Max Heap Size of 128MB (enforced via JVM args in tests).
- **TR-002**: **Concurrency**: JSON parsing must be offloaded to a separate `Scheduler` pool to avoid blocking the I/O reader.
- **TR-003**: **Cleanup**: Use `Flux.using` or similar patterns to ensure file handles are closed.
- **TR-004**: **Validation**: Implement a stress test that processes 50,000 events in under 5 seconds.

## Educational Requirements *(mandatory)*

### Concepts to Master
- **EX-001**: **The FlatMap vs. ConcatMap dilemma**: Choosing the right operator for parallel JSON parsing.
- **EX-002**: **Resource Management**: Handling `AutoCloseable` resources in a reactive flow.
- **EX-003**: **Error Recovery**: Using `onErrorContinue` vs. `onErrorResume` for data cleaning.

## Success Criteria
- **SC-001**: Successful execution of the full pipeline on a 100k-line file within 128MB Heap.
- **SC-002**: Accurate Top 5 Repository ranking.
- **SC-003**: ZERO memory leaks after multiple runs.
