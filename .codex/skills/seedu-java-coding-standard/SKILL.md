---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard when writing, changing, or reviewing Java source and test code in this project.
---

# SE-EDU Java Coding Standard

Use this skill for every task that adds, changes, or reviews Java code in this repository.

Before working on Java code, read [references/java-coding-standard.md](references/java-coding-standard.md) completely.
Apply the rules to all added or modified Java lines and audit the surrounding class for directly related violations.

## Precedence

1. Follow the user's explicit requirements and the repository's `AGENTS.md` instructions.
2. Follow the SE-EDU basic and intermediate Java coding standard.
3. For topics the SE-EDU standard does not cover, follow the Google Java Style Guide.

Do not make unrelated behavior or architecture changes merely to satisfy a stylistic preference.

## Workflow

1. Identify every Java file in scope, including tests.
2. Check naming, layout, imports, variable scope, braces, and comments against the reference.
3. Make the smallest behavior-preserving correction for existing violations unless the task requires behavior changes.
4. Check that Java lines do not exceed 120 characters and that imports are explicit and consistently grouped.
5. Run the relevant JUnit tests with Java 25. Generate Javadocs when documentation changed. If behavior or console
   output changed, also update the UI test plan and invoke the project-specific `test-ui` skill.

Use the canonical [SE-EDU basic and intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
when a rule needs clarification.
