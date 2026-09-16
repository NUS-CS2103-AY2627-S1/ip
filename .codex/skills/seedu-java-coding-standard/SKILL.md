---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding conventions to Java code in this project.
---

# SE-EDU Java coding standard

Use this standard for all Java code in this project. Apply the SE-EDU [Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html); use the Google Java Style Guide for topics it does not cover.

Key requirements:

- Use lowercase package names; PascalCase nouns for classes and enums; camelCase names for variables and verb-based methods; SCREAMING_SNAKE_CASE constants.
- Use English and American spelling. Name booleans with prefixes such as `is`, `has`, `was`, or `can`, and use plural names for collections.
- Use four spaces, K&R braces, spaces around operators and after commas, and blank lines between logical units. Keep lines at 120 characters or fewer and wrap continuation lines clearly.
- Keep imports explicit, minimal, and consistently ordered. Put every class in a package and attach array brackets to the type.
- Initialize variables at declaration when practical, keep scope minimal, encapsulate class fields, and always brace loop and conditional bodies.
- Add descriptive Javadocs to public classes and public methods, except getters/setters, applicable overrides, and test code. Use concise first-sentence summaries and correctly punctuated `@param`, `@return`, and `@throws` tags where useful.

Before finishing Java changes, inspect the diff for these rules and run the project’s tests/build with Java 25.
