---
name: seedu-java-coding-standard
description: "Review Sheppy's Java code using the SE-EDU basic and intermediate coding standard."
---

# Sheppy Java coding standard

Use this skill whenever Java source or test code is added, edited, or reviewed in this project.

## Required conventions

- Put every class in a package. Use lowercase package names.
- Use PascalCase nouns for classes and enums, lowerCamelCase verbs for methods, and lowerCamelCase for variables.
- Use `SCREAMING_SNAKE_CASE` for constants. Name collections with plural nouns.
- Use four spaces for indentation, K&R braces, and a hard line limit of 120 characters.
  Prefer wrapping before operators and indent wrapped lines by eight spaces.
- Use explicit, consistently ordered imports; never use wildcard imports.
- Put braces around every `if`, loop, and other control-flow body, including one-line bodies.
- Initialize variables at declaration when practical and keep them in the smallest useful scope.
- Write descriptive Javadoc for public classes and public methods. Start a method
  summary with a verb such as `Returns` or `Adds`, leave a blank line before tags,
  and punctuate descriptions.
- Use the project's existing style when changing nearby code, and do not reformat unrelated files.

## Checks before handoff

1. Inspect the diff and run `git diff --check`.
2. Check changed Java lines for accidental tabs and lines longer than 120 characters.
3. Run `./gradlew clean javadoc build` using Java 25.
4. If the user interface changed, update `test/ui-test-plan.md` and invoke the project `test-ui` skill.

The full reference is the [SE-EDU Java coding standard]
(https://se-education.org/guides/conventions/java/intermediate.html). Use the
Google Java Style Guide for topics not covered there.
