---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard to source and test changes in this project.
---

# SE-EDU Java Coding Standard

Use this skill for every Java source or test change in this project. It applies
the [SE-EDU basic and intermediate rules](https://se-education.org/guides/conventions/java/intermediate.html).

## Required checks

- Use English names: PascalCase nouns for types, camelCase verbs for methods,
  camelCase for variables, and UPPER_SNAKE_CASE for constants. Boolean names
  start with `is`, `has`, `was`, or a similar predicate; boolean setters use
  `setX(boolean isX)`.
- Use four-space indentation, K&R braces, explicit and consistently ordered
  imports, no wildcard imports, and source lines no longer than 120 characters.
- Keep variables in the smallest practical scope, initialize them at
  declaration where possible, and keep mutable fields non-public.
- Put braces around every loop and conditional body. Use spaces around
  operators and after commas, and separate distinct logical units with a blank
  line.
- Write English, American-spelled comments. Add descriptive Javadoc to public
  classes and public methods unless the documented behavior is an exact
  override, a trivial accessor, or test code.

Before handoff, review each edited Java file against these checks and run the
applicable Java 25 Gradle tests.
