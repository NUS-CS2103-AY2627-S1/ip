---
name: seedu-java-coding-standard
description: Apply SE-EDU Java naming, layout, statement, and comment conventions to this project.
---

# SE-EDU Java coding standard

Apply the SE-EDU basic and intermediate Java conventions to every Java source change in this
repository. The authoritative reference is the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).

## Required conventions

- Keep every class in a lower-case package; use PascalCase nouns for classes and enums.
- Use camelCase for variables and verb-based methods. Boolean names should read as predicates
  (`isDone`, `hasData`). Use SCREAMING_SNAKE_CASE for constants and plural names for collections.
- Use four spaces for indentation, K&R braces, spaces around operators and after keywords,
  commas, and `for` semicolons. Always use braces for loops and conditionals.
- Keep lines at or below 120 characters (prefer below 110), wrapping at readable boundaries with
  continuation indentation of eight spaces. Separate logical units with a blank line.
- Keep imports explicit and consistently ordered. Declare variables close to their first use and
  initialize them at declaration whenever practical.
- Add descriptive JavaDoc to every public class and public method. Getters/setters, test methods,
  and overrides whose inherited documentation applies exactly may omit it. Use a short first
  sentence, American English, and `@param`, `@return`, and `@throws` tags when they add value.

## Review checklist

Before finishing a Java change, inspect modified files for naming, whitespace, line length, braces,
imports, variable scope, and required JavaDoc. Run the project test suite after formatting changes.
