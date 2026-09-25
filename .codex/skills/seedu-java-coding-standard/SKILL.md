---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding conventions to Java source and test changes in this project.
---

# SE-EDU Java coding standard

Apply these rules to every Java change in this repository. The authoritative sources are:

- https://se-education.org/guides/conventions/java/intermediate.html
- https://se-education.org/guides/tutorials/intellijCodeStyle.html

## Required conventions

- Put every class in a lowercase package matching its directory; use explicit, consistently ordered imports.
- Use PascalCase nouns for classes and enums, camelCase for variables and verb-based methods, and SCREAMING_SNAKE_CASE for constants.
- Use plural names for collections and boolean names that read as questions, such as `isFinished` or `hasData`.
- Use four spaces for indentation, K&R braces, spaces around operators, and braces for every loop and conditional body.
- Keep lines at 120 characters or fewer; prefer wrapping at commas or before operators with an eight-space continuation indent.
- Initialize variables at declaration where practical and keep them in the smallest possible scope.
- Add descriptive Javadoc headers to every public class and public method. Include `@param`, `@return`, and `@throws` when they add useful information. Getters, setters, and applicable overrides may omit them.
- Keep comments in English, use American spelling, and separate logical blocks with blank lines.
- Use the three-part underscore convention for long test names: `featureUnderTest_testScenario_expectedBehavior()`.
- Do not use wildcard imports. Configure IntelliJ to keep import wildcard thresholds at 999 and remove trailing spaces on modified lines.

Before finishing a Java change, inspect formatting, run `git diff --check`, and run the relevant Gradle tests when available.
