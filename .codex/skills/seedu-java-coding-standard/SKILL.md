---
name: seedu-java-coding-standard
description: Enforce the SE-EDU Java coding standard (basic + intermediate) for all Java code in this project. Use when writing, reviewing, or refactoring Java classes, methods, tests, or project configuration to keep naming, layout, statements, and comments consistent with the project conventions.
---

# SE-EDU Java Coding Standard

Follow the SE-EDU Java coding standard for basic and intermediate rules as published at https://se-education.org/guides/conventions/java/intermediate.html.

This project requires all Java code to comply with these conventions whenever code is created, edited, or reviewed.

## Mandatory rules

- Use English-only identifiers and descriptive names.
- Use PascalCase for classes and enum types.
- Use camelCase for variables and methods.
- Use SCREAMING_SNAKE_CASE for constants.
- Use boolean names with `is`, `has`, `was`, `can`, or `should` where appropriate.
- Use packages in lowercase and keep imports explicit rather than wildcard imports.
- Use 4-space indentation and keep line length readable, with a hard limit of 120 characters where possible.
- Wrap blocks with braces even for single statements.
- Put conditions and loop headers in the preferred Java style with spaces around operators and keywords.
- Keep declarations in the smallest scope possible and initialize variables when appropriate.
- Use K&R/Egyptian braces and split long lines at sensible breakpoints.
- Keep Javadoc for classes and public methods, with clear summaries and `@param`/`@return` tags when helpful.
- Preserve a consistent import order and avoid unnecessary comments or dead code.

## Project enforcement

- Apply these rules to all new Java code and all existing code touched during a change.
- Prefer the smallest, most readable fix that matches the standard.
- If a section of code clearly violates the standard during a task, fix that part without broad refactoring unrelated to the request.
- Treat this as a mandatory project rule, not a suggestion.
