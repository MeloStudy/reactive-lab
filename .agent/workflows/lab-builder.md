---
description: Executes the technical implementation of a laboratory based on an approved plan and tasks (Status READY).
version: 1.0.0
---

# Workflow: Lab Builder

This workflow is triggered when a laboratory is in `READY` status and the user wants to proceed with creating code and documents.

## Agent Steps:

1. **Pre-check**:
   - Verify status is `READY`. If not, suggest running `/lab-architect` first.
   - Confirm the base environment (Docker/Node/Java) is correct according to the Constitution.

2. **Task Execution**:
   - Follow the order in `tasks.md`.
   - Implement scaffolding, seeding scripts, and test frameworks.
   - Write test code before documentation (TDD).

3. **Technical Verification**:
   - Run created tests to ensure they fail when expected and pass with the correct solution.
   - Perform a manual "Learner Journey" walkthrough.

4. **Final Documentation**:
   - Generate `README.md` as a pedagogical walkthrough. Instead of just listing commands, explain the rationale before each execution and analyze the expected output/results afterwards.
   - Generate `CONCEPT.md` providing a rigorous, deep-dive technical explanation. Cover internal reactive mechanics (e.g., event loop, signal propagation, backpressure strategies, scheduler management) to ensure a comprehensive engineering understanding, avoiding superficial definitions.
   - Complete "Command Dissection" tables.

5. **Delivery**:
   - Mark tasks as `[x]` in `tasks.md`.
   - Change status to `IMPLEMENTED`.
   - Request the user to run `/lab-auditor` for final closure.
