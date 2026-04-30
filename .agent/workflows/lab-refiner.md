---
description: Performs a comprehensive retrospective audit and end-to-end refinement of a delivered laboratory to ensure compliance with the latest standards.
version: 1.0.0
---

# Workflow: Lab Refiner

This workflow is triggered when the user wants to audit and upgrade an already delivered laboratory (usually in `AUDITED` or `IMPLEMENTED` status) to ensure it meets the latest Constitution standards (e.g., technical rigor, testing depth, naming conventions).

## Agent Steps:

1. **Retrospective Audit**:
   - Read the laboratory's current artifacts (`spec.md`, `plan.md`, `tasks.md`, `CONCEPT.md`, `README.md`, and test files).
   - Compare them strictly against the latest `docs/constitution.md` and current syllabus goals.
   - Identify gaps: e.g., superficial conceptual explanations, missing TDD coverage, lack of cleanup steps, or outdated formatting.

2. **Refinement Planning (Architect Phase)**:
   - Propose the necessary upgrades to the user.
   - Upon agreement, modify `spec.md` to include new Learning Objectives if necessary.
   - Update `plan.md` to outline the refinement strategy.
   - Generate a new, atomic checklist in `tasks.md` covering all the identified gaps.

3. **Execution (Builder Phase)**:
   - Follow the newly generated `tasks.md` to execute the retrofitting.
   - Rewrite `CONCEPT.md` to ensure deep engineering rigor.
   - Modify or add tests (TDD) to cover new requirements.
   - Update `README.md` to fix missing Command Dissections or native execution instructions.
   - Run the validation engine (e.g., `npm test` or `mvn test`) to ensure nothing is broken.

4. **Final Certification (Auditor Phase)**:
   - Perform a final conceptual gap audit on the refined deliverables.
   - Ensure the lab status is set to `AUDITED` in `spec.md`.
   - Check off all items in `tasks.md`.
   - Provide the user with a summary walkthrough of the improvements made to the laboratory.
