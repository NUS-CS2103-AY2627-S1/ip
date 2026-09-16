---
name: seedu-java-coding-standard
description: Apply the SE-EDU intermediate Java coding standard when creating, editing, or reviewing Java code in this repository.
---

# SE-EDU Java Coding Standard

Use the [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html)
for all Java production and test code. For topics it does not cover, follow the Google Java Style Guide.

## Required conventions

- Keep packages lowercase; use PascalCase nouns for types, camelCase verbs for methods, camelCase variables, and `SCREAMING_SNAKE_CASE` constants. Write names and comments in American English.
- Name booleans so they read as predicates, such as `isDone`, `hasTime`, or `canSave`. Name collections in plural. Test methods may use `featureUnderTest_testScenario_expectedBehavior`.
- Use four spaces for indentation, K&R braces, braces around every loop and conditional body, and spaces around operators and after commas.
- Keep lines at 120 characters or fewer (aim for 110). Wrap for readability, preferably after commas or before operators, and indent continuations eight spaces beyond the parent line.
- Keep imports explicit, minimal, and consistently ordered: static imports, `java`, `javax`, third-party, then project imports, with a blank line between groups.
- Declare variables in the smallest practical scope and initialize them at declaration when a valid initial value exists. Do not expose mutable class fields publicly.
- Add descriptive Javadoc to public classes and public methods, except simple accessors, exact overrides, and tests. Start with a third-person summary such as “Returns …” or “Adds …”; document non-obvious parameters, return values, and exceptions.

Before handing off Java changes, inspect affected files for these rules and run the relevant tests.
