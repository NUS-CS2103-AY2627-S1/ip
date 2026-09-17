---
name: seedu-java-coding-standard
description: Apply the SE-Education basic and intermediate Java coding conventions to this project’s Java source and tests.
---

# Seedu Java Coding Standard

Apply these rules to all Java changes in this repository. Use the
[SE-Education Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
as the authority; use Google Java Style for topics it does not cover.

- Use lowercase package names, PascalCase nouns for classes, camelCase for variables and methods, and SCREAMING_SNAKE_CASE for constants.
- Use four-space indentation, K&R braces, consistent explicit imports, and lines no longer than 120 characters.
- Initialize variables near their declarations, keep scope narrow, use braces for every loop and conditional, and document intentional switch fall-through.
- Write clear English Javadocs for public classes and methods, with a short first-sentence summary and complete useful `@param`, `@return`, and `@throws` tags.
- Test method names may use `featureUnderTest_testScenario_expectedBehavior()`.

Review existing code touched by a change for violations that can be corrected safely, and run the relevant Gradle tests and Javadoc task after edits.
