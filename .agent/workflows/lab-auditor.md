---
description: Performs the final quality review of an implemented laboratory to detect conceptual gaps and ensure pedagogical clarity.
version: 1.0.0
---

# Workflow: Lab Auditor

This workflow is triggered when a laboratory is in `IMPLEMENTED` status and needs to move to `AUDITED`.

## Agent Steps:

1. **Conceptual Gap Audit**:
   - Compare `CONCEPT.md` with `README.md`.
   - Search for "Orphan Concepts": Commands, operators, or flags used in exercises but not explained in the theory.
   - Verify that the technical level matches the syllabus module.

2. **Instruction Validation**:
   - Read `README.md` from a student's perspective. Are the instructions clear and native (no hidden wrappers)?
   - Verify that "Command Dissection" sections exist for new elements.

3. **Test Verification**:
   - Check that test files have comments explaining what is being validated.
   - Ensure tests are "focused": they should not fail for reasons unrelated to the exercise's objective.

4. **Implementation Closure**:
   - Verify all tasks in `tasks.md` are marked as `[x]`.
   - Ensure "Atomic Cleanup" is documented and functional.

5. **Certification**:
   - Update the `Status: AUDITED` field in `spec.md`.
   - Update `syllabus.md` to `Status: AUDITED`.
