---
name: seedu-java-coding-standard
description: Apply the SE-EDU intermediate Java coding standard when creating or reviewing Java production or test code in this project.
---

# Seedu Java Coding Standard

Use this skill for every Java source or test-code change in this repository. Apply the SE-EDU intermediate Java coding standard, plus the project requirements in `AGENTS.md`.

## Naming and structure

- Keep packages lowercase and rooted at `lizzy`; group related classes logically.
- Use PascalCase nouns for classes, camelCase verbs for methods, camelCase variables, and `SCREAMING_SNAKE_CASE` constants.
- Name boolean state and predicates with `is`, `has`, `can`, `should`, or an equivalent boolean phrase. Name collections in the plural.
- Keep variables in the smallest practical scope and initialize them at declaration when a valid initial value exists.

## Layout and statements

- Use four spaces for indentation, no tabs. Keep lines at or below 120 characters (prefer 110 or fewer); indent continuations by eight spaces relative to the parent line.
- Use K&R braces and braces for every loop and conditional body. Put each conditional on its own line.
- Use spaces around binary and ternary operators and after commas. Keep logical units separated by one blank line.
- Keep imports explicit and consistently ordered; do not use wildcard imports.

## Documentation

- Write English, American-spelling Javadocs for public classes and public methods unless the project’s existing documentation makes an inherited override or simple accessor self-evident.
- Start a Javadoc summary with a third-person verb such as “Returns”, “Adds”, or “Displays”. Document meaningful parameters, return values, and exceptional outcomes. Use `@inheritDoc` only when the parent contract applies unchanged.
- Preserve this project’s stronger requirement to document non-trivial private methods and fields whose purpose is not obvious.

Before completing Java changes, review the diff for these rules and run the project’s required tests. Source: https://se-education.org/guides/conventions/java/intermediate.html
