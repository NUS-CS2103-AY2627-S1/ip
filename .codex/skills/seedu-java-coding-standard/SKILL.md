---
name: seedu-java-coding-standard
description: Apply the SE-EDU intermediate Java coding standard when creating, modifying, or reviewing Java code in this project.
---

# Seedu Java Coding Standard

Use this skill for every Java change in this repository. It is based on the
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html);
use the linked guide and Google's Java Style Guide for topics not covered below.

## Naming

- Keep package names lowercase and use the project name (`kia`) as the root package.
- Use PascalCase for class and enum names, camelCase for methods and variables, and
  SCREAMING_SNAKE_CASE for constants.
- Name methods as verbs and boolean methods or variables with readable prefixes such
  as `is`, `has`, `can`, or `should`.
- Use plural names for collections and write identifiers and comments in English.
- Test method names may use the three-part form
  `featureUnderTest_testScenario_expectedBehavior()`.

## Layout and statements

- Keep `src/main/java` as the source root and put every class in a named package.
- Use four spaces for indentation, K&R braces, and braces around every conditional and
  loop body.
- Keep lines at or below 120 characters; prefer wrapping before operators with an
  eight-space continuation indent.
- Keep imports explicit, minimal, and consistently ordered; never use wildcard imports.
- Initialize variables at declaration when practical and keep them in the smallest
  scope needed.
- Separate distinct logical units in a block with one blank line.

## Documentation

- Add descriptive Javadoc to every public class and public method. Include useful
  `@param`, `@return`, and `@throws` tags, and use `{@inheritDoc}` when inherited
  documentation applies exactly.
- Write comments in English using American spelling. Explain intent and non-obvious
  decisions rather than restating straightforward code.

## Before completing a change

- Review changed Java files against these rules, especially package declarations,
  imports, naming, line length, braces, and Javadocs.
- Run the project's Java/Gradle tests and fix regressions before handing off the change.
