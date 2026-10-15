---
name: seedu-java-coding-standard
description: Apply and audit the SE-EDU Java coding standard (basic + intermediate) when creating, modifying, or reviewing Java source or test code in this repository. Do not use for tasks that do not touch Java code.
---

# SE-EDU Java Coding Standard

Apply the SE-EDU basic and intermediate Java rules to every Java change in this
repository.

Before creating, editing, or reviewing Java code, read
[the project rule summary](references/java-coding-standard.md). Use the linked
SE-EDU page as the authoritative source when exact wording or an uncovered edge
case matters. For topics absent from that standard, follow the Google Java Style
Guide as directed by SE-EDU.

## Workflow

1. Inspect the affected Java files before editing and preserve their behavior
   unless the task requires a behavior change.
2. Apply all relevant naming, layout, statement, import, and comment rules from
   the reference—not only the rule that prompted the change.
3. Keep fixes scoped to the affected code and avoid speculative refactoring.
4. Review every changed Java file against the reference, including tests.
5. Run the relevant build and tests with the project-required Java version.

When a user requirement conflicts with the standard, follow the user requirement
and briefly identify the exception.
