---
description: Orchestrates the entire planning phase from Syllabus identification to a READY state.
version: 1.0.0
---

# Workflow: Lab Master Plan

This is a macro-workflow that combines **Init** and **Architect** phases. Use this when you want to fully design a lab's specification and planning artifacts in one session.

## Steps:

1. **Phase 1: Initiation**:
   - Run the logic of `/lab-init`.
   - Create folders, copy templates, and generate the first `DRAFT` based on the Syllabus.

2. **Phase 2: Collaborative Refinement**:
   - Present the `DRAFT` to the user.
   - Ask if any specific adjustments are needed for the Learning Objectives or Scenarios.

3. **Phase 3: Architect Validation**:
   - Once the user gives the "OK" on the initial idea, run the logic of `/lab-architect`.
   - Audit the plan for "Open Questions", task granularity, and technical alignment.
   - Set status to **`READY`** and update the Syllabus.

4. **Outcome**:
   - A complete set of planning artifacts validated and ready for technical implementation.
