---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java intermediate coding standard to this repository's Java code. Use when creating, editing, reviewing, or refactoring Java source in this project so naming, layout, comments, imports, and control-flow formatting stay consistent with https://se-education.org/guides/conventions/java/intermediate.html and the repo's test-coverage policy.
---

# SE-EDU Java Coding Standard

Follow this skill for all Java code in this repository.

## Workflow

1. Read the local instructions in `AGENTS.md` first.
2. Apply the rules in `references/intermediate-rules.md`.
3. Prefer the simplest code structure that satisfies the requirement.
4. Update JUnit tests after each code change so the repository still covers at least the top ~50% highest-value methods.
5. If a Java style point is not covered here, follow the Google Java Style Guide.

## Required Rules

- Keep packages lowercase and aligned with the existing `luna.*` structure.
- Use PascalCase for classes, camelCase for methods and variables, and `UPPER_SNAKE_CASE` for constants.
- Keep boolean names readable as booleans, such as `isDone`, `hasData`, or `canParse`.
- Use 4-space indentation, K&R braces, and line wrapping that stays within the SE-EDU 120-character hard limit.
- Add descriptive Javadocs to public classes and public methods unless the method is a getter/setter, a test method, or an override whose inherited Javadoc applies exactly.
- Keep imports explicit; do not use wildcard imports.
- Use braces for all loop and conditional bodies, even for single statements.
- Separate logical units inside a method with a blank line when it improves readability.

## Applying The Standard

- When editing an existing file, preserve local naming and package structure unless there is a clear standards violation.
- When adding new methods, write the Javadoc together with the method instead of treating it as cleanup work.
- When adding tests, the underscore naming pattern `featureUnderTest_testScenario_expectedBehavior` is acceptable and preferred for long test names.
- When a rule conflicts with a repo-specific instruction in `AGENTS.md`, follow `AGENTS.md`.

## Reference

Read `references/intermediate-rules.md` when you need the detailed checklist distilled from the SE-EDU intermediate Java conventions page.
