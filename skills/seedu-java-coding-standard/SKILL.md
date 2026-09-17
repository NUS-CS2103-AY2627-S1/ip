---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java basic and intermediate coding conventions to Java code in this project.
---

# SE-EDU Java Coding Standard

Apply this skill to all Java source and test code in this repository. The authoritative
reference is the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).

## Required conventions

- Keep package names lower-case; use PascalCase for class and enum names, camelCase for
  methods and variables, and SCREAMING_SNAKE_CASE for constants.
- Use four spaces for indentation and K&R braces.
- Keep lines at or below 120 characters where reasonably possible; wrap long expressions
  at readable boundaries with wrapped continuation lines indented by eight spaces.
- Use explicit, consistently ordered imports. Do not use wildcard imports.
- Initialize variables at declaration when practical and keep them in the smallest useful scope.
- Use braces for every `if`, `else`, `for`, and `while` body, including single-statement bodies.
- Put logical statements in separate blocks with blank lines where this improves readability.
- Write descriptive English JavaDoc headers for every class and public method. Document
  non-trivial private methods as well. JavaDoc should briefly explain behavior and include
  useful `@param`, `@return`, and `@throws` details.
- Use the test naming convention `featureUnderTest_testScenario_expectedBehavior()` when
  a test name needs multiple words.

## Verification

After Java changes, run `./gradlew test` and inspect the changed files for formatting,
imports, naming, braces, and JavaDoc compliance. Update relevant JUnit tests in the same
change when behavior changes.
