---
name: seedu-java-coding-standard
description: Apply the SE-EDU intermediate Java coding standard when creating, editing, or reviewing Java code in this project.
---

# Seedu Java Coding Standard

Apply the [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html) to all Java production and test code in this repository. For topics the guide does not cover, use the Google Java Style Guide as the guide directs.

## Required checks

- Use English, American spelling, PascalCase nouns for types, camelCase verbs for methods, camelCase variables, and `SCREAMING_SNAKE_CASE` constants. Name booleans with prefixes such as `is`, `has`, or `can`, and use plural names for collections.
- Use four-space indentation, K&R braces, braces for every loop and conditional body, spaces around operators, and a maximum hard line length of 120 characters. Wrap at readable, high-level boundaries and indent continuation lines by eight spaces relative to the parent line.
- Keep imports explicit, minimal, and consistently ordered: static imports first, then standard-library imports, followed by third-party imports, with a blank line between groups.
- Keep variables in the smallest practical scope and initialize them at declaration where a valid initial value exists. Do not expose mutable class state; use private fields and methods instead.
- Write Javadoc header comments for every class and public method, unless it is a getter/setter, a test class/method, or an override whose inherited Javadoc applies unchanged. Use a short imperative summary such as “Returns …” or “Adds …”; include useful complete-sentence `@param`, `@return`, and `@throws` descriptions.
- Keep comments in English, American spelling, and at the indentation of the code they explain. Use `// Fallthrough` for intentional traditional-switch fall-through.

When editing existing code, correct violations in the touched area without changing behavior. Run the project’s formatting, style, and test checks when available.
