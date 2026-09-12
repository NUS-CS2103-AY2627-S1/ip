---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard when writing, reviewing, or refactoring Java in this project.
---

# Seedu Java Coding Standard

Apply this standard to all Java production and test code in this repository. The
authoritative reference is the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html);
use the Google Java Style Guide for topics not covered there.

## Naming

- Use lowercase package names. Use the project name (`pixel`) followed by logical
  subpackages.
- Name classes and enums with PascalCase nouns.
- Name methods with camelCase verbs and variables with camelCase.
- Name constants with SCREAMING_SNAKE_CASE.
- Use boolean names that read as predicates (`isDone`, `hasData`, `canRun`).
- Use plural names for collections.
- Keep test names in the three-part form
  `featureUnderTest_testScenario_expectedBehavior()` when a descriptive name is
  useful.

## Layout and statements

- Use four spaces for indentation and K&R braces.
- Keep lines at or below 120 characters (prefer below 110); wrapped lines use an
  additional eight spaces of indentation.
- Keep related logical blocks separated by one blank line.
- Surround operators and delimiters with the standard whitespace.
- Always use braces for loops and conditionals, including single-statement bodies.
- Keep `switch`, `try`/`catch`, and method declarations in the documented standard
  forms. Mark intentional switch fall-through explicitly.
- Initialize variables at declaration where practical and keep them in the
  smallest scope needed.

## Packages, imports, and types

- Put every class in a package.
- Keep import ordering consistent, use explicit imports, and never use wildcard
  imports.
- Attach array brackets to the type (`String[] values`).
- Keep class fields non-public to preserve encapsulation, except constants and
  deliberately behavior-free data classes.

## Documentation

- Write comments in English using American spelling.
- Add descriptive Javadoc to public classes and public methods. Getters, setters,
  exact overrides, and test methods may omit it.
- Javadoc summaries should begin with an active verb such as “Returns”, “Adds”,
  or “Sends”, followed by correctly punctuated `@param`, `@return`, and `@throws`
  descriptions when useful.

## Review workflow

When changing Java code, inspect the touched files against these rules, make the
smallest style-only corrections needed, and preserve behavior. Run the Gradle
JUnit suite and the project UI tests after code changes.
