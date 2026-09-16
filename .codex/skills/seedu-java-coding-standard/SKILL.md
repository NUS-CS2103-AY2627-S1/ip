---
name: seedu-java-coding-standard
description: "Apply the SE-EDU Java coding standard for the intermediate project conventions when editing Java files in this repo."
---

# SE-EDU Java coding standard

Use this skill for every Java code change in this repository.

## Source of truth

Follow the two SE-EDU guides:

- https://se-education.org/guides/conventions/java/intermediate.html
- https://se-education.org/guides/conventions/java/index.html

These are the project-level Java coding requirements for this codebase.

## Mandatory rules

- Package names must be lowercase.
- Class and enum names must be PascalCase.
- Method names and variable names must be camelCase.
- Constants must use SCREAMING_SNAKE_CASE.
- Use English-only names and avoid awkward acronym casing in middle of names.
- Use 4 spaces for indentation; do not use tabs.
- Keep lines readable and not longer than about 120 characters when practical.
- Use K&R/Egyptian-style braces and keep the braces on the same line as the statement.
- Include Javadoc for public classes and public methods; add Javadoc to nontrivial private methods when useful for clarity.
- Keep import statements explicit and tidy.
- Use braces for all if/else, for, while, and switch blocks even when the body is short.
- Separate logical blocks with a blank line and keep the code readable.
- Use `featureUnderTest_testScenario_expectedBehavior()` style for longer test names when needed.

## Project-specific requirement

Before finalizing Java code, ensure the result follows this standard and the repo's existing patterns.

```text
Use $seedu-java-coding-standard for all Java code changes in this project.
```

This is mandatory for all code in this repository, including model classes, parser logic, UI code, storage, and tests.
