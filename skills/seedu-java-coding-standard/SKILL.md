---
name: seedu-java-coding-standard
description: Apply the SE-EDU intermediate Java coding standard when writing, editing, or reviewing Java source in this project.
---

# SE-EDU Java Coding Standard

Apply the intermediate rules in the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html) to production Java code and tests in this project. Use the Google Java Style Guide for matters the SE-EDU guide does not cover.

## Required checks

- Use lowercase package names; use PascalCase nouns for types, camelCase verbs for methods, and camelCase for variables.
- Name boolean variables and methods with a boolean prefix such as `is`, `has`, `can`, `should`, or `was`. Name collections with plurals.
- Use `UPPER_SNAKE_CASE` for constants and avoid public mutable fields except in behavior-free data classes.
- Indent with four spaces, use K&R braces, keep lines at or below 120 characters (prefer 110 or fewer), and wrap continuations eight spaces beyond the parent indentation.
- Keep imports explicit, minimal, and consistently ordered. Do not use wildcard imports.
- Declare variables in the smallest practical scope and initialize them at declaration when a valid initial value is available.
- Always use braces for loop and conditional bodies. Keep logical units separated by one blank line when that improves readability.
- Write comments in English using American spelling. Add descriptive Javadoc to every class and public method, except self-explanatory getters/setters, tests, and overrides whose inherited documentation applies exactly.
- Format Javadoc with `/**` and `*/` on their own lines. Begin with a concise third-person summary such as “Returns”, “Adds”, or “Creates”; include useful `@param`, `@return`, and `@throws` tags.

## Applying the standard

Before finishing Java changes, inspect the affected files for violations of these rules. Correct related violations in the changed code when the correction is behavior-preserving and within the request's scope. Do not make unrelated redesigns solely for style.

For the authoritative wording and examples, consult the linked SE-EDU guide.
