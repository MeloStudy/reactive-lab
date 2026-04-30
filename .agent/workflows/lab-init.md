---
description: Initiates a new laboratory by creating the directory structure and generating the initial DRAFT artifacts based on templates.
version: 1.0.0
---

# Workflow: Lab Init

This workflow is triggered when the user wants to start a new laboratory defined in the `syllabus.md`.

## Agent Steps:

1. **Syllabus & Template Retrieval**:
   - Read `syllabus.md` to identify the target lab's objectives and concepts.
   - Read the templates from `docs/templates/` (`educational-spec-template.md`, `plan-template.md`, `tasks-template.md`).

2. **Scaffolding**:
   - Create the specification directory: `docs/specs/XXX-slug-name/`.
   - Copy and rename the templates to `spec.md`, `plan.md`, and `tasks.md`.

3. **Draft Generation**:
   - Populate `spec.md` with an initial proposal of Learning Objectives and Scenarios based on the syllabus.
   - Draft a high-level implementation strategy in `plan.md`.
   - Initialize the `tasks.md` with the standard phases (Setup, Data, TDD, Content, Finalization).
   - Set the initial status to **`DRAFT`**.

4. **Status Update**:
   - Update `syllabus.md` to reflect the lab is now `In Progress (Spec Planning)`.

5. **Completion**:
   - Present the initial draft to the user for collaborative refinement.
