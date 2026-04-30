---
description: Refines and validates the initial state of a laboratory (Spec, Plan, Tasks) to ensure it is implementable and unambiguous.
version: 1.0.0
---

# Workflow: Lab Architect

This workflow is triggered when the user wants to move a laboratory from `DRAFT` or `PLANNED` status to `READY`.

## Agent Steps:

1. **Context Retrieval**:
   - Read the laboratory's `spec.md`, `plan.md`, and `tasks.md`.
   - Read `constitution.md` to ensure compliance with core standards.

2. **Planning Audit**:
   - **Open Questions**: Verify there are no "TBD", "?", or "Decided: No" sections in `plan.md`.
   - **Task Granularity**: Ensure tasks in `tasks.md` are atomic (one task = one verifiable deliverable).
   - **Alignment**: Verify that every Learning Objective (LO) in `spec.md` has a corresponding phase in `plan.md` and assigned tasks in `tasks.md`.

3. **Automatic Refinement**:
   - Proactively fix minor inconsistencies (e.g., duplicate task IDs, missing TRs).
   - If design ambiguities are found, present them to the user as "Critical Decision Points".

4. **Status Update**:
   - Once validated, update the `Status: READY` field in `spec.md`.
   - Update `syllabus.md` to reflect that the laboratory is ready for implementation.

5. **Final Report**:
   - Summarize the changes made and confirm the lab is in `READY` status.
