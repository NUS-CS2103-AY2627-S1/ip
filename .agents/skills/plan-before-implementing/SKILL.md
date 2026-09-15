---
name: plan-before-implementing
description: Plan before implementing code, configuration, or project-structure changes in this project; do not apply to pure explanations or read-only work.
---

# Plan Before Implementing

Use this workflow whenever a request requires writing, editing, generating, or
otherwise modifying code, configuration, or project structure. Do not use it
for pure questions, explanations, reviews, or other read-only work.

## Workflow

1. Inspect enough relevant context to understand the existing design and
   repository state. Do not edit yet.
2. Present a concise, task-specific plan using exactly this structure:

   ```text
   ## Plan
   **Goal:** ...
   **Approach:** ...
   **Files touched:** ...
   **Steps:**
   1. ...
   2. ...
   **Risks/assumptions:** ...
   ```

3. Pause for confirmation before non-trivial or ambiguous work, including
   multi-file changes, new dependencies, architectural choices, and destructive
   actions. For a small, unambiguous, single-file change, state that it is
   low-risk and continue after presenting the plan.
4. After confirmation when required, implement the steps in order. Reference
   the relevant step when progress updates help the user follow the work.
5. If new information requires changing the plan, explain the change and its
   reason before continuing.

Keep a simple plan brief and make every line specific to the current task. If
the user explicitly asks to skip planning, skip it for that request only.

## Java changes

For every Java change, also read and follow
`../seedu-java-coding-standard/SKILL.md`.

Write Javadoc for every non-private class, interface, enum, constructor, and
method created or modified, including public, protected, and package-private
members. Private members do not require Javadoc. The following may omit new
Javadoc when their existing contract is already clear and unchanged:

- obvious getters and setters;
- overrides whose inherited Javadoc applies exactly;
- test classes and test methods.

Explicit user documentation requirements take precedence over these
exceptions. Use `{@inheritDoc}` plus additional notes when an override refines
or extends its inherited contract.
