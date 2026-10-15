---
name: seedu-java-coding-standard
description: Apply the SE-EDU intermediate Java coding conventions to Java code in this project.
---

# SE-EDU Java coding standard

Apply the SE-EDU Java coding standard (basic + intermediate rules) to all Java code in this
project. Use the authoritative guide for details:
https://se-education.org/guides/conventions/java/intermediate.html

- Put every class in a lowercase, logically named package; use explicit, consistently ordered imports.
- Use PascalCase nouns for classes, camelCase verbs for methods, camelCase variables, and
  SCREAMING_SNAKE_CASE constants. Name booleans as predicates (`is`, `has`, `can`).
- Use four-space indentation, K&R braces, braces around every loop and conditional body, and keep
  lines at or below 120 characters. Wrap long lines at readable boundaries.
- Initialize variables at declaration where practical, keep scope narrow, and separate logical
  units with blank lines.
- Add descriptive English Javadocs to public classes and public methods, except getters/setters,
  applicable overrides, and test methods. Use American spelling.
- Name JUnit methods with the feature/scenario convention, such as
  `parseCommand_invalidInput_exceptionThrown()`.

When editing existing code, preserve behavior and update imports, formatting, and documentation
as needed. Run the project's tests after code changes.
