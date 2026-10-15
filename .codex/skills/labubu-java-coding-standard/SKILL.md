---
name: labubu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard to all Labubu project code changes and reviews.
---

# Labubu Java coding standard

Apply these rules to all Java code in this project. Use the current source code and the configured Checkstyle rules as the local benchmark; do not change behavior merely to satisfy style.

Reference: [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html). For topics not covered there, use the Google Java Style Guide.

## Required rules

- Put every class in a lower-case package; use PascalCase nouns for classes and enums, camelCase for variables and verb-style camelCase for methods.
- Use SCREAMING_SNAKE_CASE for constants, avoid uppercase acronyms in names, use English/American spelling, and use plural names for collections.
- Use four spaces for indentation, never tabs, and K&R braces. Keep lines at or below 120 characters; wrap long lines at readable boundaries with continuation indentation.
- Keep imports explicit and consistently ordered: static imports, standard Java imports, special `org` imports, then third-party imports. Remove unused imports.
- Use braces for all loop and conditional bodies. Keep `else`/`catch` on the same line as the preceding closing brace and include `break` or an explicit fall-through comment in switch cases.
- Declare variables in the smallest practical scope, initialize them at declaration where feasible, and keep fields private unless protected access is required by the design. Prefer `final` for values that do not change.
- Add descriptive Javadoc headers to public classes and public methods. Include useful `@param`, `@return`, and `@throws` tags; getters, setters, test code, and overriding methods may omit headers when the inherited documentation applies exactly.
- Keep comments in English, indent comments with the surrounding code, and remove trailing whitespace.

## Workflow

Before finishing a code change, inspect the affected Java files for these rules and run the repository's Checkstyle task when the environment permits. Report any remaining violation with its file, line, and rule. Do not modify existing behavior or test expectations solely for formatting unless the user requests it.
