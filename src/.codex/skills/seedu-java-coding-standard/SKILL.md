---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding conventions to this project’s Java source and tests.
---

# SE-EDU Java coding standard

Apply this skill whenever you create, modify, or review Java code in this project. The authoritative reference is the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html); use the Google Java Style Guide for topics not covered there.

## Required conventions

- Put every class in a lower-case package; use project-root and logical subpackages.
- Use PascalCase nouns for classes and enums, camelCase for variables, and verb-style camelCase for methods.
- Use SCREAMING_SNAKE_CASE for constants, and use boolean names that read naturally, such as `is`, `has`, `can`, or `should`.
- Use four spaces, K&R braces, explicit imports, and a consistent import order. Keep lines at or below 120 characters and wrap long lines with continuation indentation.
- Initialize variables at declaration when practical, keep them in the smallest useful scope, and use plural names for collections.
- Always use braces for `if`, `else`, loop, and `switch` bodies. Mark intentional switch fall-through explicitly.
- Separate logical blocks with blank lines and keep whitespace around operators and after commas.
- Write comments in English using American spelling. Add descriptive Javadoc to every class and public method; use the standard `/** ... */` form with a short summary, a blank line before tags, and punctuation in descriptions. Document parameters and return values when they add useful information.
- Test method names may use `featureUnderTest_testScenario_expectedBehavior()`.

## Review workflow

Before finishing a Java change, inspect all touched Java files for these conventions, run the relevant Gradle tests with Java 25, and run the project UI tests when application code changes. Do not change behavior solely to satisfy a style rule unless the user requests it or the style violation is unambiguous and low risk.
