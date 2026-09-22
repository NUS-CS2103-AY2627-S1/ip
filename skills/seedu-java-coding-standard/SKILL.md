---
name: seedu-java-coding-standard
description: Apply the SE-EDU intermediate Java coding conventions to code in this project.
---

# Seedu Java Coding Standard

Use this skill whenever creating, modifying, or reviewing Java code in this repository.

Source: https://se-education.org/guides/conventions/java/intermediate.html

## Required conventions

- Put every class and enum in a lowercase package. Use the project name as the root package and logical subpackages only when they clarify the design.
- Name classes and enums as PascalCase nouns, methods and variables in camelCase, and constants in SCREAMING_SNAKE_CASE.
- Use English names and boolean names that read like booleans, such as `isDone`, `hasData`, or `canRun`.
- Use plural names for collections and keep variables initialized in the smallest practical scope.
- Use four-space indentation, K&R braces, spaces around operators, and braces for every loop and conditional body.
- Keep lines at 120 characters or fewer; prefer wrapping before that when it improves readability. Indent continuation lines by eight spaces relative to the parent statement.
- Keep imports explicit and consistently ordered; never use wildcard imports.
- Put array brackets next to the type, such as `String[] names`.
- Keep ordinary fields private and expose behavior through methods. Constants may be public when appropriate.
- Add descriptive Javadocs to public classes and public methods. Start summaries with forms such as “Represents”, “Returns”, “Adds”, or “Creates”. Include useful `@param`, `@return`, and `@throws` documentation, with punctuation.
- Write comments in English using American spelling. Keep comments aligned with the code and explain intent rather than restating obvious syntax.

## Review workflow

Before finishing Java changes, inspect package declarations, names, imports, line lengths, braces, field visibility, and public Javadocs. Make only changes relevant to this standard and preserve existing behavior unless the user requests a behavior change. Run the project’s tests after code changes.
