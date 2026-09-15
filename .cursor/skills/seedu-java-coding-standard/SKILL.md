---
name: seedu-java-coding-standard
description: >-
  Apply the SE-EDU Java coding standard (basic + intermediate) to all Java
  code in this project. Use when writing, editing, or reviewing Java source.
---

# SE-EDU Java Coding Standard

Follow https://se-education.org/guides/conventions/java/intermediate.html for
all Java code in this repository. Use the Google Java Style Guide for topics
not covered there.

## Naming

- Packages: all lowercase (e.g. `tem`).
- Classes/enums: PascalCase nouns.
- Methods: camelCase verbs.
- Variables: camelCase.
- Constants: `SCREAMING_SNAKE_CASE`.
- Booleans: use `is`, `has`, `can`, `should` prefixes where appropriate.
- Test methods: `featureUnderTest_testScenario_expectedBehavior()`.

## Layout

- Indent with 4 spaces, not tabs.
- Soft line-length limit: 110 characters; hard limit: 120.
- Wrap continuation lines with 8 extra spaces.
- Use K&R (Egyptian) braces.
- Put `if`, `for`, `while`, `switch`, and `try` bodies in braces even when
  they contain one statement.
- Separate logical blocks with one blank line.

## Imports

- Every class must be in a package.
- Import classes explicitly; no wildcard imports.
- Keep import order consistent within the project.

## Comments

- Write header Javadoc for all public classes and public methods.
- Use American English spelling.
- Javadoc first sentence: short summary starting with verbs like `Returns`,
  `Creates`, `Adds`.
- Include `@param`, `@return`, and `@throws` when they add value.
- Omit Javadoc for trivial getters/setters and exact `@inheritDoc` overrides.

## When editing this project

- Match existing naming and structure in nearby code.
- Apply this standard to all new and modified Java code.
- Prefer small, focused changes over large reformat-only diffs.
