---
description: Manages global changes in the Constitution and propagates updates to existing laboratories to maintain system integrity.
version: 1.0.0
---

# Workflow: Lab Governor

This workflow is triggered when feedback requires a change in project rules (Constitution) or a massive update.

## Agent Steps:

1. **Impact Analysis**:
   - Read the proposed feedback or new rule.
   - Identify which sections of `constitution.md` should be modified.
   - Search the repository for existing labs affected by this change (e.g., Java version change, new reactive testing standard).

2. **Constitution Update**:
   - Apply changes to `constitution.md`.
   - Increment document version (Minor bump).

3. **Propagation (Retrofitting)**:
   - Update `spec.md` or `plan.md` of affected laboratories.
   - Generate pending tasks in the `tasks.md` of those labs to reflect the need for implementation updates.

4. **Notification**:
   - Summarize what changed in the project's "law" and which labs need immediate attention.
