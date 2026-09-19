---
name: seedu-java-coding-standard
description: Apply the SE-Education basic and intermediate Java coding conventions to Java code in this project.
---

# SEEDU Java coding standard

Apply this skill to all Java production and test code in this repository. The
authoritative reference is the [SE-Education Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).

## Required conventions

- Put every class in a lower-case package rooted at `xerxes` and keep the
  directory structure aligned with the package declaration.
- Use PascalCase for classes, camelCase for variables and methods, and
  SCREAMING_SNAKE_CASE for constants. Boolean names should read as boolean
  questions (`isCompleted`, `hasData`, `canRun`). Collection names should be
  plural.
- Keep imports explicit and consistently ordered. Do not use wildcard imports.
- Use four spaces for indentation, K&R braces, and braces for every loop and
  conditional body. Keep lines at or below 120 characters, wrapping at
  readable boundaries with eight spaces of continuation indentation.
- Put spaces around operators, after commas, and after Java keywords such as
  `if`, `for`, and `while`. Separate logical units in a block with one blank
  line.
- Initialize variables at their declaration when practical and keep them in
  the smallest useful scope. Do not expose mutable class fields publicly.
- Write descriptive English Javadocs for public classes and public methods.
  Start the first sentence with a concise summary, and document parameters,
  return values, and exceptions when they add useful information. Getters,
  setters, overrides whose inherited documentation applies exactly, and test
  methods may omit Javadocs.
- Name test methods using `featureUnderTest_testScenario_expectedBehavior()`
  when a descriptive name needs multiple parts. Test classes belong under
  `src/test/java` in the same package structure as the production classes.

## Before handing off Java changes

- Check import order, naming, whitespace, braces, and line length manually or
  with the IDE formatter.
- Run the project's Gradle tests with Java 25:
  `./gradlew test` (or `gradlew.bat test` on Windows).
