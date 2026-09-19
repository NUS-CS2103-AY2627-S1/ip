---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java coding standard for this project when creating, editing, or reviewing Java source or JUnit tests.
---

# Seedu Java Coding Standard

Follow the SE-EDU Java coding standard, intermediate version:
https://se-education.org/guides/conventions/java/intermediate.html

For topics not covered there, follow the Google Java Style Guide.

## Apply When

Use this skill for Java and JUnit changes in this project, including code
generation, refactoring, review, and cleanup.

## Core Rules

- Put every class in a package. The source root remains `src/main/java`.
- Use package names in all lower case, rooted at the project name, such as
  `mrchatbot.parser` or `mrchatbot.task`.
- Use PascalCase nouns for class and enum names.
- Use camelCase verbs for method names.
- Use camelCase for variables and SCREAMING_SNAKE_CASE for constants.
- Name booleans so they read like booleans, preferably with prefixes such as
  `is`, `has`, `can`, `should`, or `was`.
- Use plural names for collections.
- Keep imports explicit. Do not use wildcard imports.
- Keep import ordering consistent: static imports first, then Java library
  imports, then third-party imports, then project imports, with blank lines
  between groups.
- Use 4 spaces for indentation and no tabs.
- Keep lines at or below 120 characters, with 110 characters as a soft limit.
- Wrap long lines at readable boundaries. Prefer breaking after commas and
  before operators.
- Use K&R braces: opening braces stay on the same line.
- Always use braces for loop and conditional bodies.
- Put conditionals on separate lines, not as one-line `if` statements.
- Declare variables in the smallest reasonable scope and initialize them where
  they are declared when possible.
- Separate logical units inside a block with blank lines.
- Write comments in English using American spelling and no local slang.
- Write Javadocs for public classes and public methods unless the method is a
  getter/setter, an override whose inherited Javadoc applies exactly, or test
  code.
- Javadocs should summarize what the class or method is for, not restate the
  implementation line by line.
- Test method names may use `featureUnderTest_testScenario_expectedBehavior`.

## Local Project Preferences

- Preserve existing comments unless they become inaccurate.
- Prefer small style fixes in the files being edited. Avoid large unrelated
  formatting churn unless the user explicitly asks for a coding-standard pass.
