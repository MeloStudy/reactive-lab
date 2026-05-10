# Project 1: The Reactive GitHub Insights Engine 🚀

Welcome to your first Mini-Project! In this challenge, you will move beyond isolated operators and build a real-world data processing engine.

## 🎯 The Mission
GitHub Archive (GHArchive.org) records every public event on the platform. Your goal is to build a reactive engine capable of processing JSON event files to extract intelligence about repositories and programming language trends without using heavy big-data infrastructure.

## 🛠️ Functional Requirements
You must implement a reactive pipeline that performs the following tasks on a trimmed GHArchive event file (~50,000 lines):

1.  **Ingestion & Trimming**: Read the file line-by-line. Your engine should be tested against a "Mini-Archive" of ~50k events (~50MB) to ensure it runs efficiently on standard hardware.
2.  **Event Filtering**: Process `PushEvent` (Commits), `WatchEvent` (Stars), and `PullRequestEvent` (Pull Requests). Ignore everything else.
3.  **Data Extraction**:
    - From `PushEvent`, extract the repository name and the number of commits.
    - From `WatchEvent`, extract the repository name and the actor login.
    - From `PullRequestEvent`, extract the repository name and the **Programming Language** of the repository.
4.  **Trend Analysis**:
    - Identify the **Top 5 Repositories** with the most stars in the dataset.
    - Identify the **Top 5 Programming Languages**: Aggregate the occurrences of languages in `PullRequestEvent` records and find the most active ones.
5.  **Security Enrichment (Aggregation)**:
    - Cross-reference the Top 5 repositories with a secondary security database (JSON file).
    - Report the number of known vulnerabilities for each top repository.
6.  **Resilience**:
    - If a JSON object is malformed, log the error and continue.
    - The pipeline must not stop due to individual record errors.

## ⚙️ Technical Constraints
*   **Memory Efficiency**: The application is only allowed a **128MB Heap**. You must use streams to keep memory usage low.
*   **Parallelism**: Parsing each JSON line must occur in a separate thread pool from the I/O reader to maximize throughput.
*   **Stack**: Java 21, Project Reactor, Jackson (for JSON parsing).

## 🚀 Success Criteria
Upon completion, the program must print an "Executive Summary":
1.  Total events processed.
2.  Total processing time (ms).
3.  Top 5 repositories list.
4.  Top 5 programming languages list.
5.  **Security Audit**: Vulnerability count for each top repository.

**Validation**: Pass a stress test processing at least 50,000 events in under 5 seconds.

---

## 🚀 How to Run
1.  **Navigate to the project directory**:
    ```bash
    cd projects/001-github-insights
    ```
2.  **Build the project**:
    ```bash
    mvn clean install
    ```
3.  **Execute the Engine**:
    ```bash
    mvn exec:java "-Dexec.mainClass=com.reactivelab.insights.Main" "-Dexec.args=src/test/resources/events.json"
    ```

---

## 🛠️ Getting Started
1. Download a sample from [GHArchive.org](https://www.gharchive.org/).
2. **Trim the file**: Extract the first 50,000 lines to create your development dataset.
3. Start building the pipeline!
