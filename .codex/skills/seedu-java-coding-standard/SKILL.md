---
name: seedu-java-coding-standard
description: Apply the SE-EDU intermediate Java coding standard to production Java code in this project, using Google Java Style only for gaps in that guide.
---

# SE-EDU Java Coding Standard

Use this skill whenever creating, changing, or reviewing Java code in this repository.

Follow the [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html). For subjects the guide does not cover, follow Google Java Style, as directed by the guide.

## Apply the standard

- Use English `PascalCase` type names, `camelCase` variable and method names, and `UPPER_SNAKE_CASE` constant names. Name booleans with predicates and collections with plural nouns.
- Use four spaces for indentation, K&R braces, braces for every loop and conditional body, and spaces after control keywords and around operators.
- Keep source lines at or below 120 characters. Wrap for readability with continuation indentation of eight spaces beyond the parent indentation.
- Keep imports explicit, minimal, and consistently ordered. Declare variables in the smallest useful scope and initialize them at declaration where practical.
- Write descriptive Javadocs for public classes and public non-overriding methods. Include parameter, return, and exception tags when they clarify the contract; do not duplicate obvious getter/setter or inherited contracts.
- Preserve behavior unless the requested change explicitly authorizes a functional change.

## Verify

After modifying Java code, inspect changed files for violations that automated formatting may miss, check that no Java source line exceeds 120 characters, and run the relevant project tests with Java 25.
