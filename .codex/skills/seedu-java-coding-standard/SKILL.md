---
name: seedu-java-coding-standard
description: >-
  Apply the SE-EDU Java coding standard used in CS2103/T. Use when adding,
  editing, reviewing, or refactoring Java source or test code in this project,
  and when checking whether Java code follows the SE-EDU basic + intermediate
  rules.
---

# SE-EDU Java Coding Standard

## Overview

Follow the SE-EDU Java coding standard at
https://se-education.org/guides/conventions/java/intermediate.html for all Java code in this
project. Use the Google Java Style Guide only for topics not covered by SE-EDU.

## Checklist

- Use lowercase package names rooted at the project name, such as `swell.parser`.
- Use PascalCase class and enum names, camelCase variables and methods, and SCREAMING_SNAKE_CASE
  constants.
- Name boolean variables and methods so they read as booleans, preferably with prefixes such as
  `is`, `has`, `can`, or `should`.
- Use plural names for collections.
- Indent with 4 spaces, never tabs.
- Keep lines under 120 characters, and prefer wrapping before 110 characters when readability
  improves.
- Use K&R braces: opening braces stay on the same line.
- Always use braces for loop and conditional bodies.
- Put every class in a package.
- Use explicit imports. Do not use wildcard imports.
- Keep import ordering consistent: static imports first, then Java standard library imports, then
  third-party imports, then project imports. Separate each group with one blank line.
- Declare variables in the smallest reasonable scope and initialize them where declared when a valid
  value is available.
- Write comments in English using American spelling.
- Write Javadoc header comments for all public classes and public methods, except obvious
  getters/setters, overridden methods whose inherited documentation applies exactly, and test code.
- For Javadocs, start with a short summary sentence, include `@param`, `@return`, and `@throws` only
  when they add useful information, and punctuate parameter descriptions.

## Workflow

1. Before editing Java code, check the relevant file against the checklist above.
2. Keep functional changes separate from pure coding-standard changes when possible.
3. After editing, scan for long lines, wildcard imports, missing braces, and inconsistent comments.
4. Run the relevant Gradle task, usually `./gradlew test`, before finishing.
