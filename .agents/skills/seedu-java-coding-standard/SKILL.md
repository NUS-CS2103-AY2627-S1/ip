---
name: seedu-java-coding-standard
description: Use when creating, editing, refactoring, or reviewing Java code in this project, including production code and JUnit tests, to apply the SE-EDU basic and intermediate Java coding-standard rules.
---

# SE-EDU Java Coding Standard

Keep every Java change consistent with the project's required SE-EDU basic and
intermediate coding standard.

## Required workflow

1. Before changing Java code, read
   [references/intermediate-standard.md](references/intermediate-standard.md).
2. Inspect the affected file and nearby classes for established package,
   naming, import, layout, and documentation patterns.
3. Make the smallest change that satisfies the user's request and the standard.
   Do not alter behavior merely to create a style-only change.
4. Review every touched Java line against the reference checklist.
5. Run the relevant Gradle tests. Run `gradlew javadoc` when Javadocs change.

## Project constraints

- Keep `src/main/java` and `src/test/java` as source roots; do not make `src`,
  `main`, `test`, or `java` part of a package name.
- Preserve LuigiBot's Luigi-style user-facing messages unless the user requests
  a wording change. The coding standard applies to identifiers and comments,
  not the chatbot's intentional character dialogue.
- Keep comments useful. Do not narrate obvious code merely to increase comment
  count.
- For topics absent from the SE-EDU standard, follow the Google Java Style
  Guide while preserving consistent local conventions.

## Final review

Confirm that names, indentation, wrapping, braces, imports, variable scope,
control-flow layout, and Javadocs all comply. Treat a successful compilation as
necessary but not sufficient: style rules still require a direct review.
