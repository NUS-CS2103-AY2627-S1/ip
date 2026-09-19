---
name: seedu-java-coding-standard
description: Apply the se-education intermediate Java coding standard when creating, editing, or reviewing Java code in this project.
---

# se-education Java coding standard

Apply the [se-education intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html) to all Java production and test code in this repository. For topics not covered there, use the Google Java Style Guide as the cited standard directs.

## When editing Java

- Preserve the project's package structure and public behaviour unless the user requests a behavioural change.
- Use lowercase package names, PascalCase nouns for classes and enums, camelCase verbs for methods, camelCase variables, and `UPPER_SNAKE_CASE` constants. Name boolean values and methods as predicates, and use plural names for collections.
- Keep indentation at four spaces, keep lines at or below 120 characters, use K&R braces, and put braces around every loop and conditional body. Separate logical units with blank lines.
- Keep imports explicit, minimal, and consistently ordered. Declare and initialize variables in the smallest practical scope.
- Write English (American spelling) comments. Provide Javadoc headers for public classes and public methods, except simple getters/setters, exact overrides, and test code. Use a short imperative summary such as “Returns …” or “Adds …”; include meaningful `@param`, `@return`, and `@throws` tags, with complete sentences and punctuation.

## Review scope

For a Java code change, check the modified code against these rules before completing the work. For a coding-standard audit, inspect all requested Java source files and correct applicable violations. Do not make unrelated design changes merely to satisfy a style preference.
